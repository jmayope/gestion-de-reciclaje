import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/offer_selection_controller.dart';

class OfferSelectionView extends GetView<OfferSelectionController> {
  const OfferSelectionView({super.key});
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
              Text("Cargando Residuos Publicados")
            ],
          ),
        )
      : SafeArea(
        child: controller.wastes.isEmpty
            ? Center(
                child: Padding(
                  padding: const EdgeInsets.all(32),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Container(
                        width: 90,
                        height: 90,
                        decoration: BoxDecoration(
                          color: controller.isDark.value ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
                          shape: BoxShape.circle,
                        ),
                        child: Icon(Icons.how_to_vote_outlined,
                            size: 38, color: controller.isDark.value ? AppColors.leafBright : AppColors.pine900),
                      ),
                      const SizedBox(height: 22),
                      Text('Sin ofertas por revisar',
                          textAlign: TextAlign.center, style: controller.theme.value.textTheme.headlineSmall),
                      const SizedBox(height: 8),
                      Text(
                        'Cuando los operadores oferten por tus residuos publicados, '
                        'aparecerán aquí para que elijas.',
                        textAlign: TextAlign.center,
                        style: controller.theme.value.textTheme.bodyMedium,
                      ),
                    ],
                  ),
                ),
              )
            : ListView.separated(
                physics: const BouncingScrollPhysics(),
                padding: const EdgeInsets.fromLTRB(20, 16, 20, 24),
                itemCount: controller.wastes.length,
                separatorBuilder: (_, __) => const SizedBox(height: 12),
                itemBuilder: (context, index) {
                  final waste = controller.wastes[index];
                  final wasteType = controller.wasteTypes.singleWhere((wt) => wt.code == waste.type);
                  return ResiduoOffersCard(
                    waste: waste,
                    wasteType: wasteType,
                    onTap: () async {
                      Get.offAllNamed(Routes.OFFER_SELECTION_DETAIL, arguments: {"waste": waste});
                    },
                  );
                },
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
        title: const Text('Selección de oferta'),
        centerTitle: false,
      ),
      body: container,
    );
  }
}


class ResiduoOffersCard extends StatelessWidget {
  final Waste waste;
  final TableType wasteType;
  final VoidCallback onTap;

  const ResiduoOffersCard({required this.waste, required this.wasteType, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final totalOffers = 0;
        // waste.operationOffers.fold<int>(0, (sum, o) => sum + o.offers.length);

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
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(child: Text(wasteType.name!.toUpperCase(), style: theme.textTheme.titleMedium)),
                      if ((waste.dangerousness ?? false)!)
                        const Padding(
                          padding: EdgeInsets.only(left: 6),
                          child: Icon(Icons.warning_amber_rounded, size: 16, color: AppColors.hazard),
                        ),
                    ],
                  ),
                  Text("ID #${waste.id}", style: theme.textTheme.labelSmall),
                  const SizedBox(height: 10),
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                        decoration: BoxDecoration(
                          color: AppColors.warningSoft,
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Text(
                          '${waste.pendingOperationsCount} operación(es) por decidir',
                          style: theme.textTheme.labelSmall
                              ?.copyWith(color: AppColors.warning, fontWeight: FontWeight.w700),
                        ),
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                        decoration: BoxDecoration(
                          color: isDark ? AppColors.surfaceDarkAlt : AppColors.infoSoft,
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Text(
                          '${waste.offers!.length} oferta(s) recibidas',
                          style: theme.textTheme.labelSmall
                              ?.copyWith(color: AppColors.info, fontWeight: FontWeight.w700),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(width: 8),
            Icon(Icons.chevron_right_rounded, color: AppColors.ink400),
          ],
        ),
      ),
    );
  }
}

