import 'package:ecolim_movil/app/data/additional_models/menu_item.dart';
import 'package:ecolim_movil/app/data/additional_models/stat_data.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/home_controller.dart';

class HomeView extends GetView<HomeController> {
  const HomeView({super.key});
  @override
  Widget build(BuildContext context) {

    final container = Obx(() {
      return SafeArea(
        child: CustomScrollView(
          physics: const BouncingScrollPhysics(),
          slivers: [
            SliverToBoxAdapter(
              child: UserHeader(
                initials: controller.initials.value,
                username: controller.username.value,
                companyName: controller.companyName.value,
                plantName: controller.companyType.value.code == "G" ? controller.plantName.value : null,
                companyType: controller.companyType.value,
                onLogout: () {
                  Get.offAllNamed(Routes.DEVICE_VALIDATION);
                },
                // Selector visible solo para previsualizar ambos roles
                // durante el diseño. Quitar al conectar la sesión real.
                onDebugToggleRole: () {
                  // controller.companyType.value = 
                  // setState(() {
                  //   _companyType = _companyType == CompanyType.generador
                  //       ? CompanyType.operador
                  //       : CompanyType.generador;
                  // });
                },
                isGenerator: controller.isGenerator.value,
              ),
            ),
            SliverPadding(
              padding: const EdgeInsets.fromLTRB(20, 20, 20, 4),
              sliver: SliverToBoxAdapter(
                child: Row(
                  children: List.generate(controller.stats.length, (i) {
                    final stat = controller.stats[i];
                    return Expanded(
                      child: Padding(
                        padding: EdgeInsets.only(right: i == controller.stats.length - 1 ? 0 : 10),
                        child: StatCard(stat: stat),
                      ),
                    );
                  }),
                ),
              ),
            ),
            SliverPadding(
              padding: const EdgeInsets.fromLTRB(20, 24, 20, 4),
              sliver: SliverToBoxAdapter(
                child: Text('¿Qué deseas hacer hoy?', style: controller.theme.value.textTheme.headlineSmall),
              ),
            ),
            SliverPadding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 24),
              sliver: SliverList.separated(
                itemCount: controller.menuItems.length,
                separatorBuilder: (_, __) => const SizedBox(height: 12),
                itemBuilder: (context, index) => MenuCard(item: controller.menuItems[index]),
              ),
            ),
          ],
        ),
      );
    });

    return Scaffold(
      body: container
       
    );;
  }
  
}

class UserHeader extends StatelessWidget {
  final String initials;
  final String username;
  final String companyName;
  final String? plantName;
  final TableType companyType;
  final VoidCallback onLogout;
  final VoidCallback onDebugToggleRole;
  final bool isGenerator;

  const UserHeader({
    required this.initials,
    required this.username,
    required this.companyName,
    required this.plantName,
    required this.companyType,
    required this.onLogout,
    required this.onDebugToggleRole,
    required this.isGenerator
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      margin: const EdgeInsets.fromLTRB(20, 12, 20, 0),
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(22),
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: isDark
              ? [AppColors.pine700, AppColors.surfaceDark]
              : [AppColors.pine900, AppColors.leaf500],
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 50,
                height: 50,
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Text(
                  initials,
                  style: theme.textTheme.titleLarge?.copyWith(color: AppColors.pine900),
                ),
              ),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      username,
                      style: theme.textTheme.titleLarge?.copyWith(color: Colors.white),
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 2),
                    Text(
                      companyName,
                      style: theme.textTheme.bodyMedium?.copyWith(
                        color: Colors.white.withOpacity(0.85),
                      ),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              ),
              // IconButton(
              //   tooltip: 'Vista previa de rol (dev)',
              //   icon: const Icon(Icons.sync_alt_rounded, color: Colors.white),
              //   onPressed: onDebugToggleRole,
              // ),
              IconButton(
                tooltip: 'Cerrar sesión',
                icon: const Icon(Icons.logout_rounded, color: Colors.white),
                onPressed: onLogout,
              ),
            ],
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _HeaderChip(
                icon: isGenerator ? Icons.eco_outlined : Icons.local_shipping_outlined,
                label: isGenerator ? 'Empresa Generadora' : 'Empresa Operadora',
              ),
              if (plantName != null)
                _HeaderChip(icon: Icons.factory_outlined, label: plantName!),
            ],
          ),
        ],
      ),
    );
  }
}

class _HeaderChip extends StatelessWidget {
  final IconData icon;
  final String label;
  const _HeaderChip({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.16),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 15, color: Colors.white),
          const SizedBox(width: 6),
          Text(
            label,
            style: Theme.of(context).textTheme.labelSmall?.copyWith(
                  color: Colors.white,
                  fontWeight: FontWeight.w600,
                ),
          ),
        ],
      ),
    );
  }
}

class StatCard extends StatelessWidget {
  final StatData stat;
  const StatCard({required this.stat});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 12),
      decoration: BoxDecoration(
        color: theme.cardTheme.color,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: isDark ? AppColors.line700 : AppColors.line200),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(stat.icon, size: 18, color: isDark ? AppColors.leafBright : AppColors.pine900),
          const SizedBox(height: 10),
          Text(stat.value, style: theme.textTheme.headlineSmall),
          const SizedBox(height: 2),
          Text(
            stat.label,
            style: theme.textTheme.labelSmall,
            maxLines: 2,
          ),
        ],
      ),
    );
  }
}

class MenuCard extends StatelessWidget {
  final MenuItem item;
  const MenuCard({required this.item});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return InkWell(
      onTap: item.onTap,
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
            Container(
              width: 48,
              height: 48,
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: item.accent.withOpacity(isDark ? 0.18 : 0.10),
                borderRadius: BorderRadius.circular(14),
              ),
              child: Icon(item.icon, color: item.accent, size: 24),
            ),
            const SizedBox(width: 14),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(item.title, style: theme.textTheme.titleMedium),
                  const SizedBox(height: 3),
                  Text(
                    item.subtitle,
                    style: theme.textTheme.bodyMedium,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            const SizedBox(width: 6),
            Icon(Icons.chevron_right_rounded, color: AppColors.ink400),
          ],
        ),
      ),
    );
  }
}
