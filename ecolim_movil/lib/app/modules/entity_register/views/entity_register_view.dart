import 'package:ecolim_movil/app/data/additional_models/enums.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/entity_register_controller.dart';

class EntityRegisterView extends GetView<EntityRegisterController> {
  const EntityRegisterView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            RegistrationHeader(
              step: controller.step.value,
              labels: controller.stepLabels,
              onBack: controller.handleBack,
            ),
            Expanded(
              child: PageView(
                controller: controller.pageController.value,
                physics: const NeverScrollableScrollPhysics(),
                children: [
                  _CompanyStep(
                    formKey: controller.companyFormKey,
                    razonSocial: controller.businessName,
                    ruc: controller.ruc,
                    nombreComercial: controller.companyName,
                    telefono: controller.businessPhone,
                    correo: controller.businessEmail,
                    direccion: controller.addess,
                  ),
                  _LegalRepresentativeStep(
                    formKey: controller.repFormKey,
                    nombres: controller.names,
                    apellidos: controller.lastNames,
                    numeroDocumento: controller.documentNumber,
                    correo: controller.email,
                    telefono: controller.phone,
                    tipoDocumento: controller.documentType,
                    onTipoDocumentoChanged: (v) => controller.documentType = v ?? 'DNI',
                    frontUploaded: controller.frontUploaded.value,
                    backUploaded: controller.backUploaded.value,
                    onToggleFront: () => controller.frontUploaded.value = !controller.frontUploaded.value,
                    onToggleBack: () => controller.backUploaded.value = !controller.backUploaded.value,
                  ),
                  ConfirmationStep(
                    razonSocial: controller.businessName.text,
                    ruc: controller.ruc.text,
                    direccionEmpresa: controller.addess.text,
                    nombreCompleto:
                        '${controller.names.text} ${controller.lastNames.text}'.trim(),
                    documento: '${controller.documentType} ${controller.documentNumber.text}',
                    companyKind: CompanyKind.generadora,
                    onSelectKind: (k) => controller.companyKind.value = k.name,
                    acceptTerms: controller.acceptTerms.value,
                    onAcceptTermsChanged: (v) => controller.acceptTerms.value = v ?? false),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 8, 20, 16),
              child: ElevatedButton(
                onPressed: controller.loading.value ? null : controller.handleNext,
                child: controller.loading.value
                    ? const SizedBox(
                        width: 22,
                        height: 22,
                        child: CircularProgressIndicator(
                          strokeWidth: 2.4,
                          valueColor: AlwaysStoppedAnimation(Colors.white),
                        ),
                      )
                    : Text(controller.step.value < 2 ? 'Continuar' : 'Finalizar registro'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}


class RegistrationHeader extends StatelessWidget {
  final int step;
  final List<String> labels;
  final VoidCallback onBack;

  const RegistrationHeader({required this.step, required this.labels, required this.onBack});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Padding(
      padding: const EdgeInsets.fromLTRB(12, 8, 20, 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              IconButton(
                onPressed: onBack,
                icon: const Icon(Icons.arrow_back_rounded),
              ),
              Expanded(
                child: Text(
                  'Registro de empresa',
                  style: theme.textTheme.titleLarge,
                ),
              ),
              Text(
                '${step + 1}/${labels.length}',
                style: theme.textTheme.labelSmall,
              ),
              const SizedBox(width: 8),
            ],
          ),
          const SizedBox(height: 12),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8),
            child: Row(
              children: List.generate(labels.length, (i) {
                final active = i <= step;
                return Expanded(
                  child: Padding(
                    padding: EdgeInsets.only(right: i == labels.length - 1 ? 0 : 8),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        AnimatedContainer(
                          duration: const Duration(milliseconds: 200),
                          height: 4,
                          decoration: BoxDecoration(
                            color: active
                                ? (isDark ? AppColors.leafBright : AppColors.pine900)
                                : (isDark ? AppColors.line700 : AppColors.line200),
                            borderRadius: BorderRadius.circular(4),
                          ),
                        ),
                        const SizedBox(height: 6),
                        Text(
                          labels[i],
                          style: theme.textTheme.labelSmall?.copyWith(
                            color: active
                                ? (isDark ? AppColors.leafBright : AppColors.pine900)
                                : AppColors.ink400,
                            fontWeight: active ? FontWeight.w700 : FontWeight.w500,
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              }),
            ),
          ),
        ],
      ),
    );
  }
}

/// ---------------------------------------------------------------------
/// PASO 1 — Datos generales de la empresa
/// ---------------------------------------------------------------------
class _CompanyStep extends StatelessWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController razonSocial;
  final TextEditingController ruc;
  final TextEditingController nombreComercial;
  final TextEditingController telefono;
  final TextEditingController correo;
  final TextEditingController direccion;

  const _CompanyStep({
    required this.formKey,
    required this.razonSocial,
    required this.ruc,
    required this.nombreComercial,
    required this.telefono,
    required this.correo,
    required this.direccion,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return SingleChildScrollView(
      physics: const BouncingScrollPhysics(),
      padding: const EdgeInsets.fromLTRB(20, 4, 20, 24),
      child: Form(
        key: formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text('Cuéntanos sobre tu empresa', style: theme.textTheme.headlineMedium),
            const SizedBox(height: 4),
            Text(
              'Estos datos identificarán a tu empresa en la plataforma',
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: 24),
            Text('RAZÓN SOCIAL', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: razonSocial,
              decoration: const InputDecoration(
                hintText: 'Nombre legal de la empresa',
                prefixIcon: Icon(Icons.apartment_outlined),
              ),
              validator: (v) => (v == null || v.trim().isEmpty) ? 'Ingresa la razón social' : null,
            ),
            const SizedBox(height: 18),
            Text('RUC', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: ruc,
              keyboardType: TextInputType.number,
              maxLength: 11,
              decoration: const InputDecoration(
                hintText: '11 dígitos',
                prefixIcon: Icon(Icons.badge_outlined),
                counterText: '',
              ),
              validator: (v) {
                if (v == null || v.trim().isEmpty) return 'Ingresa el RUC';
                if (v.trim().length != 11) return 'El RUC debe tener 11 dígitos';
                return null;
              },
            ),
            const SizedBox(height: 18),
            Text('NOMBRE COMERCIAL (OPCIONAL)', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: nombreComercial,
              decoration: const InputDecoration(
                hintText: 'Nombre con el que te conocen',
                prefixIcon: Icon(Icons.storefront_outlined),
              ),
            ),
            const SizedBox(height: 18),
            Text('TELÉFONO', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: telefono,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(
                hintText: 'Ej. 01 234 5678',
                prefixIcon: Icon(Icons.phone_outlined),
              ),
              validator: (v) => (v == null || v.trim().isEmpty) ? 'Ingresa un teléfono' : null,
            ),
            const SizedBox(height: 18),
            Text('CORREO ELECTRÓNICO', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: correo,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                hintText: 'contacto@empresa.com',
                prefixIcon: Icon(Icons.mail_outline_rounded),
              ),
              validator: (v) {
                if (v == null || v.trim().isEmpty) return 'Ingresa un correo';
                final ok = RegExp(r'^[\w\.\-]+@[\w\-]+\.[a-zA-Z]{2,}$').hasMatch(v.trim());
                if (!ok) return 'Correo inválido';
                return null;
              },
            ),
            const SizedBox(height: 18),
            Text('DIRECCIÓN FISCAL', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: direccion,
              decoration: const InputDecoration(
                hintText: 'Av., calle, número, distrito',
                prefixIcon: Icon(Icons.location_city_outlined),
              ),
              validator: (v) => (v == null || v.trim().isEmpty) ? 'Ingresa la dirección' : null,
            ),
          ],
        ),
      ),
    );
  }
}

/// ---------------------------------------------------------------------
/// PASO 2 — Representante legal
/// ---------------------------------------------------------------------
class _LegalRepresentativeStep extends StatelessWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController nombres;
  final TextEditingController apellidos;
  final TextEditingController numeroDocumento;
  final TextEditingController correo;
  final TextEditingController telefono;
  final String tipoDocumento;
  final ValueChanged<String?> onTipoDocumentoChanged;
  final bool frontUploaded;
  final bool backUploaded;
  final VoidCallback onToggleFront;
  final VoidCallback onToggleBack;

  const _LegalRepresentativeStep({
    required this.formKey,
    required this.nombres,
    required this.apellidos,
    required this.numeroDocumento,
    required this.correo,
    required this.telefono,
    required this.tipoDocumento,
    required this.onTipoDocumentoChanged,
    required this.frontUploaded,
    required this.backUploaded,
    required this.onToggleFront,
    required this.onToggleBack,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return SingleChildScrollView(
      physics: const BouncingScrollPhysics(),
      padding: const EdgeInsets.fromLTRB(20, 4, 20, 24),
      child: Form(
        key: formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text('Datos del representante legal', style: theme.textTheme.headlineMedium),
            const SizedBox(height: 4),
            Text(
              'Será quien administre la cuenta de la empresa',
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('NOMBRES', style: theme.textTheme.labelSmall),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: nombres,
                        decoration: const InputDecoration(hintText: 'Nombres'),
                        validator: (v) => (v == null || v.trim().isEmpty) ? 'Requerido' : null,
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('APELLIDOS', style: theme.textTheme.labelSmall),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: apellidos,
                        decoration: const InputDecoration(hintText: 'Apellidos'),
                        validator: (v) => (v == null || v.trim().isEmpty) ? 'Requerido' : null,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 18),
            Row(
              children: [
                SizedBox(
                  width: 120,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('DOCUMENTO', style: theme.textTheme.labelSmall),
                      const SizedBox(height: 8),
                      DropdownButtonFormField<String>(
                        initialValue: tipoDocumento,
                        items: const ['DNI', 'CE', 'Pasaporte']
                            .map((t) => DropdownMenuItem(value: t, child: Text(t)))
                            .toList(),
                        onChanged: onTipoDocumentoChanged,
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('NÚMERO', style: theme.textTheme.labelSmall),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: numeroDocumento,
                        keyboardType: TextInputType.text,
                        decoration: const InputDecoration(hintText: 'Número de documento'),
                        validator: (v) => (v == null || v.trim().isEmpty) ? 'Requerido' : null,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 18),
            Text('CORREO ELECTRÓNICO', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: correo,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                hintText: 'tucorreo@ejemplo.com',
                prefixIcon: Icon(Icons.mail_outline_rounded),
              ),
              validator: (v) {
                if (v == null || v.trim().isEmpty) return 'Ingresa un correo';
                final ok = RegExp(r'^[\w\.\-]+@[\w\-]+\.[a-zA-Z]{2,}$').hasMatch(v.trim());
                if (!ok) return 'Correo inválido';
                return null;
              },
            ),
            const SizedBox(height: 18),
            Text('TELÉFONO', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextFormField(
              controller: telefono,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(
                hintText: 'Ej. 987 654 321',
                prefixIcon: Icon(Icons.phone_outlined),
              ),
              validator: (v) => (v == null || v.trim().isEmpty) ? 'Ingresa un teléfono' : null,
            ),
            const SizedBox(height: 26),
            Text('IDENTIFICACIÓN DE DOCUMENTO', style: theme.textTheme.labelSmall),
            const SizedBox(height: 4),
            Text(
              'Sube ambas caras de tu $tipoDocumento',
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                Expanded(
                  child: _DocumentUploadBox(
                    label: 'Anverso',
                    uploaded: frontUploaded,
                    onTap: onToggleFront,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _DocumentUploadBox(
                    label: 'Reverso',
                    uploaded: backUploaded,
                    onTap: onToggleBack,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _DocumentUploadBox extends StatelessWidget {
  final String label;
  final bool uploaded;
  final VoidCallback onTap;

  const _DocumentUploadBox({required this.label, required this.uploaded, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(16),
      child: Container(
        height: 110,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          color: uploaded
              ? (isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft)
              : (isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight),
          border: Border.all(
            color: uploaded
                ? (isDark ? AppColors.leafBright : AppColors.pine900)
                : (isDark ? AppColors.line700 : AppColors.line200),
            width: uploaded ? 1.6 : 1,
          ),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              uploaded ? Icons.check_circle_rounded : Icons.add_a_photo_outlined,
              size: 26,
              color: uploaded
                  ? (isDark ? AppColors.leafBright : AppColors.pine900)
                  : AppColors.ink400,
            ),
            const SizedBox(height: 8),
            Text(
              uploaded ? '$label cargado' : label,
              style: theme.textTheme.labelSmall,
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}

/// ---------------------------------------------------------------------
/// PASO 3 — Confirmación + tipo de empresa
/// ---------------------------------------------------------------------
class ConfirmationStep extends StatelessWidget {
  final String razonSocial;
  final String ruc;
  final String direccionEmpresa;
  final String nombreCompleto;
  final String documento;
  final CompanyKind? companyKind;
  final ValueChanged<CompanyKind> onSelectKind;
  final bool acceptTerms;
  final ValueChanged<bool?> onAcceptTermsChanged;

  const ConfirmationStep({
    required this.razonSocial,
    required this.ruc,
    required this.direccionEmpresa,
    required this.nombreCompleto,
    required this.documento,
    required this.companyKind,
    required this.onSelectKind,
    required this.acceptTerms,
    required this.onAcceptTermsChanged,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return SingleChildScrollView(
      physics: const BouncingScrollPhysics(),
      padding: const EdgeInsets.fromLTRB(20, 4, 20, 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text('Revisa y confirma', style: theme.textTheme.headlineMedium),
          const SizedBox(height: 4),
          Text(
            'Verifica que todo esté correcto antes de enviar tu registro',
            style: theme.textTheme.bodyMedium,
          ),
          const SizedBox(height: 22),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: theme.cardTheme.color,
              borderRadius: BorderRadius.circular(18),
              border: Border.all(color: isDark ? AppColors.line700 : AppColors.line200),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('EMPRESA', style: theme.textTheme.labelSmall),
                const SizedBox(height: 8),
                _SummaryLine(label: 'Razón social', value: razonSocial),
                _SummaryLine(label: 'RUC', value: ruc),
                _SummaryLine(label: 'Dirección', value: direccionEmpresa),
                const Divider(height: 26),
                Text('REPRESENTANTE LEGAL', style: theme.textTheme.labelSmall),
                const SizedBox(height: 8),
                _SummaryLine(label: 'Nombre', value: nombreCompleto),
                _SummaryLine(label: 'Documento', value: documento),
              ],
            ),
          ),
          const SizedBox(height: 26),
          Text('TIPO DE EMPRESA', style: theme.textTheme.labelSmall),
          const SizedBox(height: 4),
          Text('Selecciona el rol con el que operará tu empresa', style: theme.textTheme.bodyMedium),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: _CompanyKindCard(
                  label: 'Generadora',
                  description: 'Genera y publica residuos',
                  icon: Icons.eco_outlined,
                  selected: companyKind == CompanyKind.generadora,
                  onTap: () => onSelectKind(CompanyKind.generadora),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _CompanyKindCard(
                  label: 'Operadora',
                  description: 'Oferta y procesa residuos',
                  icon: Icons.local_shipping_outlined,
                  selected: companyKind == CompanyKind.operadora,
                  onTap: () => onSelectKind(CompanyKind.operadora),
                ),
              ),
            ],
          ),
          const SizedBox(height: 22),
          InkWell(
            onTap: () => onAcceptTermsChanged(!acceptTerms),
            borderRadius: BorderRadius.circular(12),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Checkbox(
                  value: acceptTerms,
                  onChanged: onAcceptTermsChanged,
                  activeColor: AppColors.pine900,
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.only(top: 12),
                    child: Text(
                      'He verificado que la información es correcta y acepto los '
                      'términos y condiciones del servicio.',
                      style: theme.textTheme.bodyMedium,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _SummaryLine extends StatelessWidget {
  final String label;
  final String value;
  const _SummaryLine({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 100, child: Text(label, style: theme.textTheme.bodyMedium)),
          Expanded(
            child: Text(
              value.isEmpty ? '—' : value,
              style: theme.textTheme.titleMedium,
            ),
          ),
        ],
      ),
    );
  }
}

class _CompanyKindCard extends StatelessWidget {
  final String label;
  final String description;
  final IconData icon;
  final bool selected;
  final VoidCallback onTap;

  const _CompanyKindCard({
    required this.label,
    required this.description,
    required this.icon,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(16),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: selected
              ? (isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft)
              : (isDark ? AppColors.surfaceDarkAlt : Colors.white),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: selected
                ? (isDark ? AppColors.leafBright : AppColors.pine900)
                : (isDark ? AppColors.line700 : AppColors.line200),
            width: selected ? 1.6 : 1,
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon,
                color: selected ? (isDark ? AppColors.leafBright : AppColors.pine900) : AppColors.ink400),
            const SizedBox(height: 10),
            Text(label, style: theme.textTheme.titleMedium),
            const SizedBox(height: 2),
            Text(description, style: theme.textTheme.bodyMedium),
          ],
        ),
      ),
    );
  }
}
