import 'dart:math';

import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LoginController extends GetxController {
  //TODO: Implement LoginController

  final count = 0.obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final formKey = GlobalKey<FormState>();
  final username = TextEditingController();
  final password = TextEditingController();
  final hiddenPassword = true.obs;
  final logging = false.obs;
  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    theme.value = Theme.of(Get.context!);
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

  Future<void> login() async {
    bool needChangePassword = false; //  Random().nextInt(2) > 1;
    if (needChangePassword) {
      Get.offAllNamed(Routes.CHANGE_PASSWORD);
    } else {
      bool isGenerator = Random().nextInt(2) > 1;
      if (isGenerator) {
        bool selectPlant = Random().nextInt(3) > 1;
        if (selectPlant) {
          Get.offAllNamed(Routes.SELECT_PLANT);
        }
      } else {
        Get.offAllNamed(Routes.HOME);
      }
    }
  }
}
