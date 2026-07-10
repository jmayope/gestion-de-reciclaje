import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/offer.dart';
import 'package:ecolim_movil/models/process_flow.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/offer_selection_detail_controller.dart';

class OfferSelectionDetailView extends GetView<OfferSelectionDetailController> {
  const OfferSelectionDetailView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAllNamed(Routes.OFFER_SELECTION);
          }, 
          icon: Icon(Icons.home)
        ),
        title: Text(controller.waste.value.type!),
        centerTitle: false,
      ),
      body: SafeArea(
        child: ListView(
          physics: const BouncingScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 32),
          children: [
            _ResiduoSummary(waste: controller.waste.value),
            const SizedBox(height: 24),
            for (final group in []) ...[
              OperationSection(group: group, onSelect: (offer) => controller.confirmSelection(group, offer)),
              const SizedBox(height: 22),
            ],
          ],
        ),
      ),
    );
  }
}



class _ResiduoSummary extends StatelessWidget {
  final Waste waste;
  const _ResiduoSummary({required this.waste});

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
      child: Row(
        children: [
          Container(
            width: 46,
            height: 46,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
              borderRadius: BorderRadius.circular(14),
            ),
            child: Icon(Icons.inventory_2_outlined,
                color: isDark ? AppColors.leafBright : AppColors.pine900),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(waste.id.toString(), style: theme.textTheme.labelSmall),
                Text('${waste.quantity} ton totales', style: theme.textTheme.bodyMedium),
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
              child: Text('Peligroso',
                  style: theme.textTheme.labelSmall
                      ?.copyWith(color: AppColors.hazard, fontWeight: FontWeight.w700)),
            ),
        ],
      ),
    );
  }
}

class OperationSection extends StatelessWidget {
  final ProcessFlow group;
  final ValueChanged<Offer> onSelect;

  const OperationSection({required this.group, required this.onSelect});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(Icons.ac_unit_rounded, size: 18, color: isDark ? AppColors.leafBright : AppColors.pine900),
            const SizedBox(width: 8),
            Text(group.currentProcessId!.toString(), style: theme.textTheme.titleLarge),
          ],
        ),
        const SizedBox(height: 10),
        if (group.accepted != null)
          AcceptedOfferCard(offer: Offer())
        else if (group.offers!.isEmpty)
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight,
              borderRadius: BorderRadius.circular(14),
              border: Border.all(color: isDark ? AppColors.line700 : AppColors.line200),
            ),
            child: Text('Aún no hay ofertas para esta operación', style: theme.textTheme.bodyMedium),
          )
        else
          Column(
            children: group.offers!
                .map((offer) => Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: _OfferCard(offer: offer, onSelect: () => onSelect(offer)),
                    ))
                .toList(),
          ),
      ],
    );
  }
}

class _OfferCard extends StatelessWidget {
  final Offer offer;
  final VoidCallback onSelect;

  const _OfferCard({required this.offer, required this.onSelect});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: theme.cardTheme.color,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: isDark ? AppColors.line700 : AppColors.line200),
      ),
      child: Row(
        children: [
          CircleAvatar(
            radius: 18,
            backgroundColor: isDark ? AppColors.surfaceDarkAlt : AppColors.bgLight,
            child: Icon(Icons.local_shipping_outlined,
                size: 17, color: isDark ? AppColors.textDarkSecondary : AppColors.ink600),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(offer.createdBy.toString(), style: theme.textTheme.titleMedium),
                const SizedBox(height: 2),
                Text(
                  '${offer.quantity} ton · ${formatDate(offer.createdAt!)}',
                  style: theme.textTheme.bodyMedium,
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          OutlinedButton(
            onPressed: onSelect,
            style: OutlinedButton.styleFrom(
              minimumSize: const Size(0, 40),
              padding: const EdgeInsets.symmetric(horizontal: 14),
              side: const BorderSide(color: AppColors.pine900),
            ),
            child: const Text('Seleccionar'),
          ),
        ],
      ),
    );
  }

}

class AcceptedOfferCard extends StatelessWidget {
  final Offer offer;
  const AcceptedOfferCard({required this.offer});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: isDark ? AppColors.leafBright : AppColors.pine900, width: 1.4),
      ),
      child: Row(
        children: [
          Icon(Icons.check_circle_rounded, color: isDark ? AppColors.leafBright : AppColors.pine900),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Oferta aceptada', style: theme.textTheme.labelSmall?.copyWith(
                      color: isDark ? AppColors.leafBright : AppColors.pine900,
                      fontWeight: FontWeight.w700,
                    )),
                Text(offer.createdBy.toString(), style: theme.textTheme.titleMedium),
                Text('${offer.quantity} ton', style: theme.textTheme.bodyMedium),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
