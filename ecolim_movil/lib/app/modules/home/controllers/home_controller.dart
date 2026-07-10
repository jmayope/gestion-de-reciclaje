import 'package:ecolim_movil/app/data/additional_models/menu_item.dart';
import 'package:ecolim_movil/app/data/additional_models/stat_data.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/app/theme/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:get/get.dart';

class HomeController extends GetxController {
  //TODO: Implement HomeController

  final count = 0.obs;
  final initials = 'JM'.obs;
  final username = 'lookmmm'.obs;
  final companyName = 'Pepito SAC'.obs;
  final companyType = TableType().obs;
  final plantName = ''.obs;

  final theme = ThemeData().obs;
  final isDark = false.obs;
  final isGenerator = false.obs;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    theme.value =  Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    companyType.value = TableType(code: "O");
    isGenerator.value = companyType.value.code == "G";
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  List<StatData> get stats {
    if (companyType.value.code == "G") {
      return const [
        StatData(label: 'Registrados', value: '18', icon: Icons.inventory_2_outlined),
        StatData(label: 'Publicados', value: '5', icon: Icons.campaign_outlined),
        StatData(label: 'Ofertas por revisar', value: '3', icon: Icons.local_offer_outlined),
      ];
    }
    return const [
      StatData(label: 'Ofertas activas', value: '7', icon: Icons.local_offer_outlined),
      StatData(label: 'En seguimiento', value: '4', icon: Icons.timeline_outlined),
      StatData(label: 'Completados', value: '12', icon: Icons.task_alt_outlined),
    ];
  }

  List<MenuItem> get menuItems {
    if (companyType.value.code == "G") {
      return [
        MenuItem(
          icon: Icons.inventory_2_outlined,
          title: 'Administración de Residuos',
          subtitle: 'Registra, agrupa y da de baja tus residuos',
          accent: AppColors.pine900,
          onTap: () {
            Get.offAllNamed(Routes.WASTE_MANAGEMENT);
          },
        ),
        MenuItem(
          icon: Icons.campaign_outlined,
          title: 'Publicación de Residuos',
          subtitle: 'Publica los residuos agrupados para su oferta',
          accent: AppColors.leaf500,
          onTap: () {
            Get.offAllNamed(Routes.WASTE_PUBLISH);
          },
        ),
        MenuItem(
          icon: Icons.sell_outlined,
          title: 'Selección de Ofertas',
          subtitle: 'Selecciona la mejor oferta para el proceso del residuo',
          accent: AppColors.leaf500,
          onTap: () {
            Get.offAllNamed(Routes.OFFER_SELECTION);
          },
        ),
        MenuItem(
          icon: Icons.insert_chart_outlined_rounded,
          title: 'Reportería',
          subtitle: 'Residuos registrados, publicados y ofertados',
          accent: AppColors.info,
          onTap: () {
            Get.offAllNamed(Routes.REPORTING);
          },
        ),
      ];
    }
    return [
      MenuItem(
        icon: Icons.local_offer_outlined,
        title: 'Ofertar Residuos',
        subtitle: 'Encuentra residuos publicados y postula tu oferta',
        accent: AppColors.pine900,
        onTap: () {
          Get.offAllNamed(Routes.WASTE_OFFER);
        },
      ),
      MenuItem(
        icon: Icons.timeline_outlined,
        title: 'Seguimiento',
        subtitle: 'Estado de residuos con oferta aceptada',
        accent: AppColors.leaf500,
        onTap: () {
          Get.offAllNamed(Routes.WASTE_TRACKING);
        },
      ),
      MenuItem(
        icon: Icons.insert_chart_outlined_rounded,
        title: 'Reportería',
        subtitle: 'Historial de ofertas realizadas',
        accent: AppColors.info,
        onTap: () {
          Get.offAllNamed(Routes.REPORTING);
        },
      ),
    ];
  }

  void increment() => count.value++;
}
