import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_tracking_controller.dart';

class WasteTrackingView extends GetView<WasteTrackingController> {
  const WasteTrackingView({super.key});
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
        title: const Text('Seguimiento'),
        centerTitle: false,
      ),
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 8),
              child: Row(
                children: [
                  Expanded(
                    child: _ToggleChip(
                      label: 'En curso',
                      selected: controller.onlyInProgress.value,
                      onTap: () => controller.onlyInProgress.value = true,
                    ),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: _ToggleChip(
                      label: 'Todos',
                      selected: !controller.onlyInProgress.value,
                      onTap: () => controller.onlyInProgress.value = false,
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              child: controller.filtereds.isEmpty
                  ? Center(
                      child: Text('No hay seguimientos en curso', style: controller.theme.value.textTheme.bodyMedium),
                    )
                  : ListView.separated(
                      physics: const BouncingScrollPhysics(),
                      padding: const EdgeInsets.fromLTRB(20, 8, 20, 24),
                      itemCount: controller.filtereds.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 12),
                      itemBuilder: (context, index) {
                        final tracking = controller.filtereds[index];
                        return _TrackingCard(
                          tracking: tracking,
                          onTap: () async {
                            Get.offAllNamed(Routes.WASTE_TRACKING_DETAIL, arguments: {"waste"});
                          },
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

class _ToggleChip extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;

  const _ToggleChip({required this.label, required this.selected, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(14),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.symmetric(vertical: 11),
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: selected
              ? AppColors.pine900
              : (isDark ? AppColors.surfaceDarkAlt : Colors.white),
          borderRadius: BorderRadius.circular(14),
          border: Border.all(
            color: selected ? AppColors.pine900 : (isDark ? AppColors.line700 : AppColors.line200),
          ),
        ),
        child: Text(
          label,
          style: theme.textTheme.labelLarge?.copyWith(
            color: selected ? Colors.white : (isDark ? AppColors.textDarkSecondary : AppColors.ink600),
          ),
        ),
      ),
    );
  }
}

class _TrackingCard extends StatelessWidget {
  final Waste tracking;
  final VoidCallback onTap;

  const _TrackingCard({required this.tracking, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final stage = ""; // TrackingStage.values[tracking.currentStageIndex];
    final progress = 20.0; //(tracking.currentStageIndex + 1) / TrackingStage.values.length;

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
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(tracking.type!, style: theme.textTheme.titleMedium),
                      const SizedBox(height: 2),
                      Text(
                        '${tracking.id} · ${tracking.createdBy}',
                        style: theme.textTheme.bodyMedium,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                  decoration: BoxDecoration(
                    color: tracking.status!
                        ? AppColors.leafSoft
                        : (isDark ? AppColors.surfaceDarkAlt : AppColors.infoSoft),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(Icons.layers_outlined,
                          size: 13, color: tracking.status! ? AppColors.pine900 : AppColors.info),
                      const SizedBox(width: 5),
                      Text(
                        stage,
                        style: theme.textTheme.labelSmall?.copyWith(
                          color: tracking.status! ? AppColors.pine900 : AppColors.info,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Icon(Icons.tips_and_updates_rounded, size: 14, color: AppColors.ink400),
                const SizedBox(width: 5),
                Text(tracking.type!, style: theme.textTheme.bodyMedium),
                const SizedBox(width: 12),
                Icon(Icons.scale_outlined, size: 14, color: AppColors.ink400),
                const SizedBox(width: 5),
                Text('${tracking.quantity} ton', style: theme.textTheme.bodyMedium),
              ],
            ),
            const SizedBox(height: 12),
            ClipRRect(
              borderRadius: BorderRadius.circular(6),
              child: LinearProgressIndicator(
                value: progress,
                minHeight: 6,
                backgroundColor: isDark ? AppColors.line700 : AppColors.line200,
                valueColor: AlwaysStoppedAnimation(
                  tracking.status!
                      ? AppColors.leaf500
                      : (isDark ? AppColors.leafBright : AppColors.pine900),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
