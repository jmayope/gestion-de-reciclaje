import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../models/type.dart';
class PlanRegisterController extends GetxController {
  //TODO: Implement PlanRegisterController

  final count = 0.obs;
  final formKey = GlobalKey<FormState>().obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;

  final name = TextEditingController();
  final description = TextEditingController();
  final phone = TextEditingController();
  final email = TextEditingController();
  final address = TextEditingController();
  final latitude = TextEditingController();
  final longitude = TextEditingController();

  final plantTypeSelected = Type().obs;
  final plantTypes = <Type>[].obs;
  final locationPicked = false.obs;
  final loading = false.obs;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
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
    

    // TODO: enviar datos al backend y regresar a Selección de Planta con
    // la nueva planta ya disponible / preseleccionada.
    await Future.delayed(const Duration(milliseconds: 3200));

    loading.value = false;

    Get.offAllNamed(Routes.SELECT_PLANT);

  }

  void increment() => count.value++;
}
