import 'package:ecolim_movil/app/data/additional_models/operation_slot.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_offer_controller.dart';

class WasteOfferView extends GetView<WasteOfferController> {
  const WasteOfferView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAllNamed(Routes.HOME);
          }, 
          icon: Icon(Icons.home)
        ),
        title: const Text('Ofertar residuos'),
        centerTitle: false,
      ),
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 8),
              child: TextField(
                onChanged: (v) => controller.query.value = v,
                decoration: const InputDecoration(
                  hintText: 'Buscar por tipo, generador o código',
                  prefixIcon: Icon(Icons.search_rounded),
                ),
              ),
            ),
            Expanded(
              child: controller.filtereds.isEmpty
                  ? Center(
                      child: Text('No se encontraron residuos publicados',
                          style: controller.theme.value.textTheme.bodyMedium),
                    )
                  : ListView.separated(
                      physics: const BouncingScrollPhysics(),
                      padding: const EdgeInsets.fromLTRB(20, 8, 20, 24),
                      itemCount: controller.filtereds.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 14),
                      itemBuilder: (context, index) {
                        final waste = controller.filtereds[index];
                        return _OfferableWasteCard(
                          waste: waste,
                          onOperationTap: (slot) => controller.openOfferSheet(waste, slot),
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
    );
  }
}


class _OfferableWasteCard extends StatelessWidget {
  final Waste waste;
  final ValueChanged<TableType> onOperationTap;

  const _OfferableWasteCard({required this.waste, required this.onOperationTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: theme.cardTheme.color,
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: isDark ? AppColors.line700 : AppColors.line200),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 34,
                height: 34,
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight,
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(Icons.domain_outlined,
                    size: 17, color: isDark ? AppColors.textDarkSecondary : AppColors.ink600),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Text(
                  waste.createdBy.toString(),
                  style: theme.textTheme.bodyMedium?.copyWith(fontWeight: FontWeight.w600),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              Text(_timeAgo(waste.publishAt!), style: theme.textTheme.labelSmall),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(waste.type.toString(), style: theme.textTheme.titleLarge),
                    const SizedBox(height: 2),
                    Text(waste.id.toString(), style: theme.textTheme.labelSmall),
                  ],
                ),
              ),
              if (waste.dangerousness!)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 5),
                  decoration: BoxDecoration(
                    color: AppColors.hazardSoft,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.warning_amber_rounded, size: 13, color: AppColors.hazard),
                      const SizedBox(width: 4),
                      Text('Peligroso',
                          style: theme.textTheme.labelSmall
                              ?.copyWith(color: AppColors.hazard, fontWeight: FontWeight.w700)),
                    ],
                  ),
                ),
            ],
          ),
          const SizedBox(height: 6),
          Row(
            children: [
              Icon(Icons.scale_outlined, size: 14, color: AppColors.ink400),
              const SizedBox(width: 5),
              Text('${waste.quantity} ton disponibles', style: theme.textTheme.bodyMedium),
            ],
          ),
          const SizedBox(height: 14),
          Text('OPERACIONES DISPONIBLES', style: theme.textTheme.labelSmall),
          const SizedBox(height: 8),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: waste.operations!.map((slot) {
              final offered = slot.status == OfferStatus.yaOfertado;
              return InkWell(
                onTap: offered ? null : () => onOperationTap(slot),
                borderRadius: BorderRadius.circular(20),
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 9),
                  decoration: BoxDecoration(
                    color: offered
                        ? (isDark ? AppColors.surfaceDarkAlt : AppColors.line200.withOpacity(0.5))
                        : (isDark ? AppColors.surfaceDarkAlt : Colors.white),
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(
                      color: offered
                          ? (isDark ? AppColors.line700 : AppColors.line200)
                          : AppColors.pine900,
                    ),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        Icons.list_alt,
                        size: 15,
                        color: offered
                            ? AppColors.ink400
                            : (isDark ? AppColors.leafBright : AppColors.pine900),
                      ),
                      const SizedBox(width: 6),
                      Text(
                        slot.name!,
                        style: theme.textTheme.labelLarge?.copyWith(
                          color: offered
                              ? AppColors.ink400
                              : (isDark ? AppColors.leafBright : AppColors.pine900),
                        ),
                      ),
                      if (offered) ...[
                        const SizedBox(width: 6),
                        Icon(Icons.check_circle_rounded, size: 14, color: AppColors.ink400),
                      ] else ...[
                        const SizedBox(width: 6),
                        Icon(Icons.add_circle_outline_rounded,
                            size: 14, color: isDark ? AppColors.leafBright : AppColors.pine900),
                      ],
                    ],
                  ),
                ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }

  String _timeAgo(DateTime date) {
    final diff = DateTime.now().difference(date).inDays;
    if (diff <= 0) return 'Hoy';
    if (diff == 1) return 'Ayer';
    return 'Hace $diff días';
  }
}

/// Hoja para registrar la oferta sobre una operación específica.
class OfferFormSheet extends StatefulWidget {
  final Waste waste;
  final TableType slot;

  const OfferFormSheet({required this.waste, required this.slot});

  @override
  State<OfferFormSheet> createState() => OfferFormSheetState();
}

class OfferFormSheetState extends State<OfferFormSheet> {
  final _formKey = GlobalKey<FormState>();
  final _quantityController = TextEditingController();
  bool _loading = false;

  @override
  void dispose() {
    _quantityController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _loading = true);

    // TODO: enviar la oferta al backend (cantidad + operación + residuo).
    await Future.delayed(const Duration(milliseconds: 1000));

    if (!mounted) return;
    setState(() => _loading = false);
    Navigator.of(context).pop(true);
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Padding(
      padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: Container(
        padding: const EdgeInsets.fromLTRB(20, 12, 20, 24),
        decoration: BoxDecoration(
          color: theme.scaffoldBackgroundColor,
          borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
        ),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  margin: const EdgeInsets.only(bottom: 18),
                  decoration: BoxDecoration(
                    color: isDark ? AppColors.line700 : AppColors.line200,
                    borderRadius: BorderRadius.circular(4),
                  ),
                ),
              ),
              Row(
                children: [
                  Container(
                    width: 44,
                    height: 44,
                    alignment: Alignment.center,
                    decoration: BoxDecoration(
                      color: AppColors.leafSoft,
                      borderRadius: BorderRadius.circular(14),
                    ),
                    child: Icon(Icons.list_alt_outlined, color: AppColors.pine900),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('Ofertar por ${widget.slot.name}',
                            style: theme.textTheme.titleLarge),
                        Text('${widget.waste.type} · ${widget.waste.id}',
                            style: theme.textTheme.bodyMedium),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 18),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(Icons.domain_outlined, size: 16, color: AppColors.ink400),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(widget.waste.createdBy.toString(), style: theme.textTheme.bodyMedium),
                    ),
                    Text('${widget.waste.quantity} Tn', style: theme.textTheme.bodyMedium),
                  ],
                ),
              ),
              const SizedBox(height: 18),
              Text('CANTIDAD A OFERTAR (TONELADAS)', style: theme.textTheme.labelSmall),
              const SizedBox(height: 8),
              TextFormField(
                controller: _quantityController,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                decoration: const InputDecoration(
                  hintText: 'Ej. 2.0',
                  prefixIcon: Icon(Icons.scale_outlined),
                  suffixText: 'ton',
                ),
                validator: (v) {
                  if (v == null || v.trim().isEmpty) return 'Ingresa la cantidad a ofertar';
                  final n = double.tryParse(v.trim());
                  if (n == null || n <= 0) return 'Cantidad inválida';
                  if (n > widget.waste.quantity!) {
                    return 'No puede superar los ${widget.waste.quantity} ton disponibles';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 14),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: isDark ? AppColors.surfaceDarkAlt : AppColors.infoSoft,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Icon(Icons.info_outline_rounded,
                        size: 17, color: isDark ? AppColors.textDarkSecondary : AppColors.info),
                    const SizedBox(width: 9),
                    Expanded(
                      child: Text(
                        'El generador revisará todas las ofertas recibidas para esta '
                        'operación y decidirá cuál aceptar.',
                        style: theme.textTheme.bodyMedium,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton(
                      onPressed: _loading ? null : () => Navigator.of(context).pop(false),
                      child: const Text('Cancelar'),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: _loading ? null : _submit,
                      child: _loading
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2.2,
                                valueColor: AlwaysStoppedAnimation(Colors.white),
                              ),
                            )
                          : const Text('Enviar oferta'),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

