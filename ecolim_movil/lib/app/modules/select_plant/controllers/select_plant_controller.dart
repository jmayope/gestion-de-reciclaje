import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/plant.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SelectPlantController extends GetxController {
  //TODO: Implement SelectPlantController

  final count = 0.obs;
  final loading = true.obs;
  final hasPlants = false.obs;
  final plants = <Plant>[].obs;
  final query = ''.obs;
  final selectedId = int.parse('0').obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    theme.value =  Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    Future.delayed(Duration(seconds: 4), () {
      loading.value = false;
      hasPlants.value = false;
    });
  }

  List<Plant> get filtered {
    if (query.value.trim().isEmpty) return plants;
    final q = query.toLowerCase();
    return plants
        .where((p) =>
            p.name!.toLowerCase().contains(q) ||
            p.address!.toLowerCase().contains(q))
        .toList();
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  Future<void> goToPlantRegistration() async {
    Get.offAllNamed(Routes.PLAN_REGISTER);
  }

  Future<void> continueToDashboard() async {
    if (selectedId == null) return;
    Get.offAllNamed(Routes.HOME);
  }

  void increment() => count.value++;
}
