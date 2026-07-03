import 'package:ecolim_movil/models/index.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WasteRegisterController extends GetxController {
  //TODO: Implement WasteRegisterController

  final count = 0.obs;
  final isEditing = false.obs;
  final formKey = GlobalKey<FormState>().obs;

  final wasteTypeSelected = TableType().obs;
  final wasteTypes = <TableType>[].obs;


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
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  void increment() => count.value++;
}
