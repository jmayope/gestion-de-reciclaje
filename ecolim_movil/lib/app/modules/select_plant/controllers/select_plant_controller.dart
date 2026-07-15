import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/plant.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class SelectPlantController extends GetxController {
  
  final loading = true.obs;
  final hasPlants = false.obs;
  final plants = <Plant>[].obs;
  final query = ''.obs;
  final selectedId = int.parse('0').obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final userLoged = User().obs;
  final supabase = Get.put(SupabaseService());

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    loading.value = true;
    query.value = '';
    plants.value = [];
    theme.value =  Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    userLoged.value = await PreferenceService.getSession();
    final resultPlants = await supabase.select(PLANTS, filters: {"entity_id": userLoged.value.currentEntity!.id!});
    print(resultPlants);
    plants.value = (resultPlants as Iterable).map((p) => Plant.fromJson(p)).toList();
    loading.value = false;
    hasPlants.value = plants.isNotEmpty;
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
    Plant plantSelected = plants.singleWhere((p) => p.id!.isEqual(selectedId.value));
    userLoged.value = userLoged.value.copyWith(currentPlant: Optional.of(plantSelected));
    bool saved = await PreferenceService.setSession(userLoged.value);
    Get.offAllNamed(Routes.HOME);
  }
}
