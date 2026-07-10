import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/index.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WasteRegisterController extends GetxController {
  //TODO: Implement WasteRegisterController

  final count = 0.obs;
  final isEditing = false.obs;
  final formKey = GlobalKey<FormState>().obs;
  final loading = false.obs;
  final wasteTypeSelected = TableType().obs;
  final wasteTypes = <TableType>[].obs;
  final wasteOperations = <TableType>[].obs;
  final quantity = TextEditingController();
  final observation = TextEditingController();
  final wasteGenerationDate = DateTime.now().obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final hasStorage = false.obs;

  final wasteOperationSelecteds = <TableType>[].obs;


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

  Future<void> pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: Get.context!,
      initialDate: wasteGenerationDate.value ?? now,
      firstDate: DateTime(now.year - 2),
      lastDate: now,
    );
    if (picked != null)  {
      wasteGenerationDate.value = picked;
    }
  }

  Future<void> handleSubmit() async {
    if (!formKey.value.currentState!.validate()) return;
    if (wasteGenerationDate.value == null) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Selecciona la fecha de generación')),
      );
      return;
    }
    if (wasteOperationSelecteds.isEmpty) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Selecciona al menos una operación del residuo')),
      );
      return;
    }

    loading.value = true;
    // TODO: enviar datos al backend (crear o actualizar según _isEditing).
    // Si _hasStorage == false -> el residuo queda listo para "Publicado".
    // Si _hasStorage == true  -> el residuo queda en estado "Agrupado".
    await Future.delayed(const Duration(milliseconds: 1200));
    loading.value = false;
    Get.offAllNamed(Routes.WASTE_MANAGEMENT);
  }

  
}
