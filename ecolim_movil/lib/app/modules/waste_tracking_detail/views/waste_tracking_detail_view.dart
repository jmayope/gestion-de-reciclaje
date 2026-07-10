import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/index.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_tracking_detail_controller.dart';

class WasteTrackingDetailView extends GetView<WasteTrackingDetailController> {
  const WasteTrackingDetailView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Get.offAllNamed(Routes.WASTE_TRACKING);
          }, 
          icon: Icon(Icons.arrow_back)
        ),
        title: Text(controller.waste.value.type!),
        centerTitle: false,
      ),
      body: SafeArea(
        child: ListView(
          physics: const BouncingScrollPhysics(),
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 32),
          children: [
            TrackingSummary(tracking: controller.waste.value),
            const SizedBox(height: 28),
            Text('LÍNEA DE TIEMPO', style: controller.theme.value.textTheme.labelSmall),
            const SizedBox(height: 14),
            ...controller.processFlows.map((p) {
              int i = controller.processFlows.indexOf(p);
              return Column(
                children: [
                  TimelineTile(
                    stage: p,
                    date: p.createdAt!,
                    isLast: i == controller.processFlows.length - 1,
                    isCompleted: p != null,
                    isCurrent: i == 1,
                  ),
                  const SizedBox(height: 12),
                  if (!p.status!)
                    ElevatedButton.icon(
                      onPressed: controller.advanceStage,
                      icon: const Icon(Icons.arrow_forward_rounded, size: 19),
                      label: Text('Marcar como "${controller.waste.value.processFlows![1 + 1].currentProcessId}"'),
                    )
                  else
                    Container(
                      padding: const EdgeInsets.all(14),
                      decoration: BoxDecoration(
                        color: AppColors.leafSoft,
                        borderRadius: BorderRadius.circular(14),
                      ),
                      child: Row(
                        children: const [
                          Icon(Icons.task_alt_rounded, color: AppColors.pine900),
                          SizedBox(width: 10),
                          Expanded(
                            child: Text(
                              'Este residuo completó todo el proceso de seguimiento.',
                              style: TextStyle(color: AppColors.pine900, fontWeight: FontWeight.w600),
                            ),
                          ),
                        ],
                      ),
                    ),
                ],
              );
            })
          ],
        ),
      ),
    );
  }
}


class TrackingSummary extends StatelessWidget {
  final Waste tracking;
  const TrackingSummary({required this.tracking});

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
                width: 44,
                height: 44,
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: isDark ? AppColors.surfaceDarkAlt : AppColors.leafSoft,
                  borderRadius: BorderRadius.circular(14),
                ),
                child: Icon(Icons.leaderboard,
                    color: isDark ? AppColors.leafBright : AppColors.pine900),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(tracking.type!, style: theme.textTheme.titleMedium),
                    Text(tracking.id.toString(), style: theme.textTheme.labelSmall),
                  ],
                ),
              ),
            ],
          ),
          const Divider(height: 28),
          _SummaryRow(icon: Icons.domain_outlined, label: 'Generador', value: tracking.createdBy!.toString()),
          const SizedBox(height: 10),
          _SummaryRow(
              icon: Icons.scale_outlined, label: 'Cantidad', value: '${tracking.quantity} ton'),
        ],
      ),
    );
  }
}

class _SummaryRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _SummaryRow({required this.icon, required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Row(
      children: [
        Icon(icon, size: 16, color: AppColors.ink400),
        const SizedBox(width: 8),
        Text(label, style: theme.textTheme.bodyMedium),
        const Spacer(),
        Text(value, style: theme.textTheme.titleMedium),
      ],
    );
  }
}

class TimelineTile extends StatelessWidget {
  final ProcessFlow stage;
  final DateTime? date;
  final bool isLast;
  final bool isCompleted;
  final bool isCurrent;

  const TimelineTile({
    required this.stage,
    required this.date,
    required this.isLast,
    required this.isCompleted,
    required this.isCurrent,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final activeColor = isDark ? AppColors.leafBright : AppColors.pine900;

    return IntrinsicHeight(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Column(
            children: [
              Container(
                width: 34,
                height: 34,
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: isCompleted
                      ? activeColor
                      : (isDark ? AppColors.surfaceDarkAlt : AppColors.line200.withOpacity(0.5)),
                  border: isCurrent && !isCompleted ? Border.all(color: activeColor, width: 1.6) : null,
                ),
                child: Icon(
                  Icons.storage,
                  size: 16,
                  color: isCompleted ? Colors.white : AppColors.ink400,
                ),
              ),
              if (!isLast)
                Expanded(
                  child: Container(
                    width: 2,
                    margin: const EdgeInsets.symmetric(vertical: 2),
                    color: isCompleted ? activeColor.withOpacity(0.4) : AppColors.line200,
                  ),
                ),
            ],
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Padding(
              padding: EdgeInsets.only(bottom: isLast ? 0 : 24, top: 4),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    stage.currentProcessId!,
                    style: theme.textTheme.titleMedium?.copyWith(
                      color: isCompleted ? null : AppColors.ink400,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    date != null ? formatDate(date!) : 'Pendiente',
                    style: theme.textTheme.bodyMedium,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

}
