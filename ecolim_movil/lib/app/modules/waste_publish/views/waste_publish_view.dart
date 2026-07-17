import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/index.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';
import 'package:quiver/core.dart';

import '../controllers/waste_publish_controller.dart';

class WastePublishView extends GetView<WastePublishController> {
  const WastePublishView({super.key});
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
              Text("Cargando Residuos publicados")
            ],
          ),
        )
      : SafeArea(
        child: controller.hasWastes.value
            ? Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(20, 12, 20, 4),
                    child: Container(
                      padding: const EdgeInsets.all(14),
                      decoration: BoxDecoration(
                        color: controller.isDark.value ? AppColors.surfaceDarkAlt : AppColors.infoSoft,
                        borderRadius: BorderRadius.circular(14),
                      ),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Icon(Icons.info_outline_rounded,
                              size: 18, color: controller.isDark.value ? AppColors.textDarkSecondary : AppColors.info),
                          const SizedBox(width: 10),
                          Expanded(
                            child: Text(
                              'Selecciona los residuos registrados que deseas publicar. '
                              'Una vez publicados, los operadores podrán ofertar por ellos.',
                              style: controller.theme.value.textTheme.bodyMedium,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  Expanded(
                    child: ListView.separated(
                      physics: const BouncingScrollPhysics(),
                      padding: const EdgeInsets.fromLTRB(20, 14, 20, 110),
                      itemCount: controller.wastes.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 12),
                      itemBuilder: (context, index) {
                        final waste = controller.wastes[index];
                        final wasteType = controller.wasteTypes.singleWhere((wt) => wt.code == waste.type);
                        return SelectableWasteCard(
                          waste: waste,
                          wasteType: wasteType,
                          selected: (waste.selected ?? false),
                          onTap: () async {
                            controller.wastes[index] = waste.copyWith(selected: Optional.of(!(waste.selected ?? false)!));
                            await controller.toggle(waste.id.toString());
                          },
                        );
                      },
                    ),
                  ),
                ],
              )
            : const EmptyPublicationState(),
      );
    });

    final navigator = Obx(() {
      return controller.hasWastes.value
          ? SafeArea(
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 180),
                padding: const EdgeInsets.fromLTRB(20, 12, 20, 16),
                decoration: BoxDecoration(
                  color: controller.theme.value.scaffoldBackgroundColor,
                  border: Border(
                    top: BorderSide(color: controller.isDark.value ? AppColors.line700 : AppColors.line200),
                  ),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Text(
                        controller.selecteds.isEmpty
                            ? 'Ningún residuo seleccionado'
                            : '${controller.selecteds.length} residuo(s) seleccionado(s)',
                        style: controller.theme.value.textTheme.bodyMedium,
                      ),
                    ),
                    const SizedBox(width: 12),
                    ElevatedButton.icon(
                      onPressed: controller.selecteds.isEmpty ? null : controller.confirmPublish,
                      icon: const Icon(Icons.campaign_outlined, size: 19),
                      label: const Text('Publicar'),
                    ),
                  ],
                ),
              ),
            )
          : Container();
    });

    final actions = [
      Obx(() {
        if (!controller.hasWastes.value) return const SizedBox.shrink();
        
        return TextButton(
          onPressed: () {
            for (int i = 0; i < controller.wastes.length; i++) {
              controller.wastes[i] = controller.wastes[i].copyWith(
                selected: Optional.of(!(controller.wastes[i].selected ?? false))
              );
            }
            controller.toggleAll();
          },
          child: Text(controller.allSelected ? 'Ninguno' : 'Todos'),
        );
      }),
    ];

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAllNamed(Routes.HOME);
          }, 
          icon: Icon(Icons.arrow_back)
        ),
        title: const Text('Publicación de residuos'),
        centerTitle: false,
        actions: actions,
      ),
      body: container,
      bottomNavigationBar: navigator,
    );
  }
}


class SelectableWasteCard extends StatelessWidget {
  final Waste waste;
  final TableType wasteType;
  final bool selected;
  final VoidCallback onTap;

  const SelectableWasteCard({
    required this.waste,
    required this.wasteType,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(18),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: selected
              ? (isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft)
              : theme.cardTheme.color,
          borderRadius: BorderRadius.circular(18),
          border: Border.all(
            color: selected
                ? (isDark ? AppColors.leafBright : AppColors.pine900)
                : (isDark ? AppColors.line700 : AppColors.line200),
            width: selected ? 1.6 : 1,
          ),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(
              selected ? Icons.check_circle_rounded : Icons.circle_outlined,
              color: selected
                  ? (isDark ? AppColors.leafBright : AppColors.pine900)
                  : AppColors.ink400,
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: Text(wasteType.name!.toUpperCase(), style: theme.textTheme.titleMedium),
                      ),
                      if ((waste.dangerousness?? false))
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
                          decoration: BoxDecoration(
                            color: AppColors.hazardSoft,
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Icon(Icons.warning_amber_rounded,
                                  size: 12, color: AppColors.hazard),
                              const SizedBox(width: 3),
                              Text('Peligroso',
                                  style: theme.textTheme.labelSmall?.copyWith(
                                      color: AppColors.hazard, fontWeight: FontWeight.w700)),
                            ],
                          ),
                        ),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Text("ID #${waste.id}", style: theme.textTheme.labelSmall),
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      Icon(Icons.scale_outlined, size: 14, color: AppColors.ink400),
                      const SizedBox(width: 5),
                      Text('${waste.quantity} ${waste.unitMeasurement}', style: theme.textTheme.bodyMedium),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(Icons.inventory_2_outlined, size: 14, color: AppColors.ink400),
                      const SizedBox(width: 5),
                      Expanded(
                        child: Text(
                          '${(waste.hasStorageLocation ?? false) ? "TIENE" : "NO TIENE"} LUGAR DE ALMACENAMIENTO',
                          style: theme.textTheme.bodyMedium,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class PublishConfirmSheet extends StatelessWidget {
  final List<Waste> wastes;
  final List<TableType> wasteTypes;
  const PublishConfirmSheet({required this.wastes, required this.wasteTypes});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final totalTon = wastes.fold<double>(0, (sum, w) => sum + w.quantity!);
    return Padding(
      padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: Container(
        constraints: BoxConstraints(maxHeight: MediaQuery.of(context).size.height * 0.8),
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
                    color: AppColors.leafSoft,
                    borderRadius: BorderRadius.circular(14),
                  ),
                  child: const Icon(Icons.campaign_outlined, color: AppColors.pine900),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Confirmar publicación', style: theme.textTheme.titleLarge),
                      Text(
                        '${wastes.length} residuo(s) · ${totalTon.toStringAsFixed(1)} Tn/Kg en total',
                        style: theme.textTheme.bodyMedium,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Flexible(
              child: ListView.separated(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: wastes.length,
                separatorBuilder: (_, __) => const Divider(height: 20),
                itemBuilder: (context, index) {
                  final w = wastes[index];
                  final wt = wasteTypes.singleWhere((wt) => wt.code == w.type);
                  return Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(wt.name!.toString().toUpperCase(), style: theme.textTheme.titleMedium),
                            Text("ID #${w.id}", style: theme.textTheme.labelSmall),
                          ],
                        ),
                      ),
                      Text('${w.quantity} ${w.unitMeasurement}', style: theme.textTheme.bodyMedium),
                    ],
                  );
                },
              ),
            ),
            const SizedBox(height: 18),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: isDark ? AppColors.surfaceDarkAlt : AppColors.infoSoft,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Icon(Icons.visibility_outlined,
                      size: 18, color: isDark ? AppColors.textDarkSecondary : AppColors.info),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'Estos residuos serán visibles para todos los operadores registrados '
                      'en la plataforma.',
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
                    onPressed: () => Get.close(1),
                    child: const Text('Cancelar'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => Navigator.of(context).pop(true),
                    child: const Text('Confirmar y publicar'),
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

class EmptyPublicationState extends StatelessWidget {
  const EmptyPublicationState();

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 90,
              height: 90,
              decoration: BoxDecoration(
                color: isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.campaign_outlined,
                size: 38,
                color: isDark ? AppColors.leafBright : AppColors.pine900,
              ),
            ),
            const SizedBox(height: 22),
            Text(
              'No tienes residuos registrados',
              textAlign: TextAlign.center,
              style: theme.textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Text(
              'Cuando un residuo tenga lugar de almacenamiento asignado, '
              'aparecerá aquí listo para publicarse.',
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyMedium,
            ),
          ],
        ),
      ),
    );
  }
}
