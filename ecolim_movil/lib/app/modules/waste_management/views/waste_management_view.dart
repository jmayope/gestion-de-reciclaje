import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/index.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_management_controller.dart';

class WasteManagementView extends GetView<WasteManagementController> {
  const WasteManagementView({super.key});
  @override
  Widget build(BuildContext context) {

    final container = Obx(() {
      return controller.loading.value ?
        Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              CircularProgressIndicator(),
              Text(
                "Cargando Residuos"
              )
            ],
          ),
        )
      : SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 8),
              child: TextField(
                onChanged: (v) { controller.query.value = v; },
                decoration: const InputDecoration(
                  hintText: 'Buscar por tipo o código',
                  prefixIcon: Icon(Icons.search_rounded),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    // _FilterChip(
                    //   label: 'Todos',
                    //   selected: _filter == null,
                    //   onTap: () => setState(() => _filter = null),
                    // ),
                    // const SizedBox(width: 8),
                    // ...WasteStatus.values.map((s) => Padding(
                    //       padding: const EdgeInsets.only(right: 8),
                    //       child: _FilterChip(
                    //         label: s.label,
                    //         selected: _filter == s,
                    //         color: s.color,
                    //         onTap: () => setState(() => _filter = s),
                    //       ),
                    //     )),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 8),
            Expanded(
              child: controller.filtered.isEmpty
                  ? Center(
                      child: Text(
                        'No se encontraron residuos',
                        style: controller.theme.value.textTheme.bodyMedium,
                      ),
                    )
                  : ListView.separated(
                      physics: const BouncingScrollPhysics(),
                      padding: const EdgeInsets.fromLTRB(20, 4, 20, 96),
                      itemCount: controller.filtered.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 12),
                      itemBuilder: (context, index) {
                        final waste = controller.filtered[index];
                        final wasteType = controller.wasteTypes.singleWhere((wt) => wt.code == waste.type);
                        final unitMeasurement = controller.unitMeasurements.singleWhere((u) => u.code == waste.unitMeasurement);
                        return WasteCard(
                          waste: waste,
                          wasteType: wasteType,
                          unitMeasurement: unitMeasurement,
                          onTap: () => waste.state != "R" ? null : controller.goToDetail(waste),
                          onWithdraw: waste.state == "R" || waste.state == "A" ? () => controller.requestWithdrawal(waste)
                              : null,
                        );
                      },
                    ),
            ),
          ],
        ),
      );
    });

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAllNamed(Routes.HOME);
          }, 
          icon: Icon(Icons.home)
        ),
        title: const Text('Administración de residuos'),
        centerTitle: false,
      ),
      floatingActionButton: FloatingActionButton.extended(
        backgroundColor: Colors.white,
        onPressed: controller.goToRegisterWaste,
        icon: const Icon(Icons.add_rounded, color: AppColors.leaf500),
        label: const Text(
          'Registrar residuo',
          style: TextStyle(
            color: AppColors.leaf500
          )
        ),
      ),
      body: container,
    );
  }
}


class _FilterChip extends StatelessWidget {
  final String label;
  final bool selected;
  final Color? color;
  final VoidCallback onTap;

  const _FilterChip({
    required this.label,
    required this.selected,
    required this.onTap,
    this.color,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final activeColor = color ?? AppColors.pine900;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(20),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 9),
        decoration: BoxDecoration(
          color: selected
              ? activeColor
              : (isDark ? AppColors.surfaceDarkAlt : Colors.white),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: selected
                ? activeColor
                : (isDark ? AppColors.line700 : AppColors.line200),
          ),
        ),
        child: Text(
          label,
          style: theme.textTheme.labelLarge?.copyWith(
            color: selected
                ? Colors.white
                : (isDark ? AppColors.textDarkSecondary : AppColors.ink600),
          ),
        ),
      ),
    );
  }
}

class WasteCard extends StatelessWidget {
  final Waste waste;
  final TableType unitMeasurement;
  final TableType wasteType;
  final VoidCallback onTap;
  final VoidCallback? onWithdraw;

  const WasteCard({required this.waste, required this.unitMeasurement, required this.wasteType, required this.onTap, required this.onWithdraw});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(18),
      child: Container(
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
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("ID: #${waste.id}", style: theme.textTheme.labelSmall),
                      const SizedBox(height: 2),
                      Text(wasteType.name!.toUpperCase(), style: theme.textTheme.titleMedium),
                    ],
                  ),
                ),
                StatusBadge(state: waste.state!),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                _InfoPill(icon: Icons.scale_outlined, label: '${waste.quantity} ${unitMeasurement.name!}'),
                const SizedBox(width: 8),
                _InfoPill(
                  icon: Icons.event_outlined,
                  label: _formatDate(waste.wasteGenerationDate!),
                ),
                const SizedBox(width: 8),
                // if (waste.hazardous)
                //   Container(
                //     padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 5),
                //     decoration: BoxDecoration(
                //       color: AppColors.hazardSoft,
                //       borderRadius: BorderRadius.circular(20),
                //     ),
                //     child: Row(
                //       mainAxisSize: MainAxisSize.min,
                //       children: [
                //         const Icon(Icons.warning_amber_rounded, size: 13, color: AppColors.hazard),
                //         const SizedBox(width: 4),
                //         Text(
                //           'Peligroso',
                //           style: theme.textTheme.labelSmall
                //               ?.copyWith(color: AppColors.hazard, fontWeight: FontWeight.w700),
                //         ),
                //       ],
                //     ),
                //   ),
              ],
            ),
            if (waste.state == "E") ...[
              const SizedBox(height: 12),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                decoration: BoxDecoration(
                  color: isDark ? AppColors.surfaceDarkAlt : AppColors.warningSoft,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.hourglass_top_rounded, size: 15, color: AppColors.warning),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        'Baja en revisión — pendiente de autorización',
                        style: theme.textTheme.labelSmall,
                      ),
                    ),
                  ],
                ),
              ),
            ] else if (onWithdraw != null) ...[
              const SizedBox(height: 12),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton.icon(
                  onPressed: onWithdraw,
                  icon: const Icon(Icons.remove_circle_outline_rounded, size: 17),
                  label: const Text('Dar de baja'),
                  style: TextButton.styleFrom(foregroundColor: AppColors.hazard),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  String _formatDate(DateTime date) {
    const months = [
      'ene', 'feb', 'mar', 'abr', 'may', 'jun',
      'jul', 'ago', 'sep', 'oct', 'nov', 'dic',
    ];
    return '${date.day} ${months[date.month - 1]} ${date.year}';
  }
}

class StatusBadge extends StatelessWidget {
  final String state;
  const StatusBadge({required this.state});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      decoration: BoxDecoration(
        color: AppColors.leaf300,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        state,
        style: Theme.of(context).textTheme.labelSmall?.copyWith(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.w700,
            ),
      ),
    );
  }
}

class _InfoPill extends StatelessWidget {
  final IconData icon;
  final String label;
  const _InfoPill({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 5),
      decoration: BoxDecoration(
        color: isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 13, color: isDark ? AppColors.textDarkSecondary : AppColors.ink600),
          const SizedBox(width: 4),
          Text(label, style: theme.textTheme.labelSmall),
        ],
      ),
    );
  }
}

/// Hoja inferior de confirmación para solicitar la baja de un residuo.
/// Deja explícito que la baja no es inmediata: requiere autorización.
class WithdrawalSheet extends StatelessWidget {
  final Waste waste;
  final TextEditingController reasonController;

  const WithdrawalSheet({required this.waste, required this.reasonController});

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
                    color: AppColors.hazardSoft,
                    borderRadius: BorderRadius.circular(14),
                  ),
                  child: const Icon(Icons.remove_circle_outline_rounded, color: AppColors.hazard),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Dar de baja ${waste.id}', style: theme.textTheme.titleLarge),
                      Text(waste.type!, style: theme.textTheme.bodyMedium),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: isDark ? AppColors.surfaceDarkAlt : AppColors.warningSoft,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Icon(Icons.info_outline_rounded, size: 18, color: AppColors.warning),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'Esta acción no es inmediata: se enviará una solicitud '
                      'que debe ser autorizada antes de completarse.',
                      style: theme.textTheme.bodyMedium,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 18),
            Text('MOTIVO DE LA BAJA', style: theme.textTheme.labelSmall),
            const SizedBox(height: 8),
            TextField(
              controller: reasonController,
              maxLines: 3,
              decoration: const InputDecoration(
                hintText: 'Describe brevemente el motivo',
                alignLabelWithHint: true,
              ),
            ),
            const SizedBox(height: 20),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: () => Navigator.of(context).pop(false),
                    child: const Text('Cancelar'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ValueListenableBuilder<TextEditingValue>(
                    valueListenable: reasonController,
                    builder: (context, value, _) {
                      return ElevatedButton(
                        style: ElevatedButton.styleFrom(backgroundColor: AppColors.hazard),
                        onPressed: value.text.trim().isEmpty
                            ? null
                            : () => Navigator.of(context).pop(true),
                        child: const Text('Enviar solicitud'),
                      );
                    },
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

