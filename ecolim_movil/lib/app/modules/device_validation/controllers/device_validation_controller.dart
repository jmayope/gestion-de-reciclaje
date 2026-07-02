import 'dart:math';

import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class DeviceValidationController extends GetxController with GetSingleTickerProviderStateMixin {
  //TODO: Implement DeviceValidationController

  final count = 0.obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  late final AnimationController pulseController;
  final statusText = 'Verificando Dispositivo'.obs;
  final afiliated = false.obs;
  final verifying = true.obs;
  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    pulseController = AnimationController(vsync: this, duration: Duration(milliseconds: 1100))..repeat(reverse: true);
    theme.value = Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    Future.delayed(Duration(seconds: 4), () {
      statusText.value = "Comprobando Afiliación de dispositivo";
      afiliated.value = true;
      verifying.value = false;
      if (afiliated.value) {
        Get.offAllNamed(Routes.LOGIN);
      } else {
        statusText.value = "Dispositivo NO Afiliado";
      }
    });
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    pulseController.stop();
    pulseController.dispose();
    super.onClose();
  }

  void increment() => count.value++;
}
