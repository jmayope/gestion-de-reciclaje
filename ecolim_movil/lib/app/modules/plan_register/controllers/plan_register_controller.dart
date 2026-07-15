import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../models/type.dart';
class PlanRegisterController extends GetxController {
  //TODO: Implement PlanRegisterController

  final count = 0.obs;
  final formKey = GlobalKey<FormState>().obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;

  final code = TextEditingController();
  final name = TextEditingController();
  final phone = TextEditingController();
  final address = TextEditingController();
  final latitude = TextEditingController();
  final longitude = TextEditingController();
  final locationPicked = false.obs;
  final loading = false.obs;
  final userLoged = User().obs;
  final supabase = Get.put(SupabaseService());

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    userLoged.value = await PreferenceService.getSession();
    theme.value =  Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  Future<void> useCurrentLocation() async {
    // TODO: integrar geolocator para obtener la ubicación real del dispositivo.
    latitude.text = '-12.046374';
    longitude.text = '-77.042793';
    locationPicked.value = true;
  }

  Future<void> openMapPicker() async {
    // TODO: abrir selector visual (google_maps_flutter) para elegir el punto
    // exacto tocando el mapa, y sincronizar con _latController/_lngController.
    useCurrentLocation();
  }

  Future<void> handleSubmit() async {
    if (!formKey.value.currentState!.validate()) return;
    if (!locationPicked.value) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Selecciona la ubicación de la planta en el mapa')),
      );
      return;
    }
    loading.value = true;

    final newPlant = {
      "code": code.value.text,
      "name": name.value.text,
      "address": address.value.text,
      "phone": phone.value.text,
      "latitude": latitude.value.text,
      "longitude": longitude.value.text,
      "entity_id": userLoged.value.currentEntity!.id!,
      "status": false
    };

    final resultPlants = await supabase.insert(PLANTS, newPlant);
    ScaffoldMessenger.of(Get.context!).showSnackBar(
      const SnackBar(content: Text('Planta creada correctamente')),
    );
    await Future.delayed(const Duration(milliseconds: 2200));

    loading.value = false;

    Get.offAllNamed(Routes.SELECT_PLANT);

  }

  void increment() => count.value++;
}
