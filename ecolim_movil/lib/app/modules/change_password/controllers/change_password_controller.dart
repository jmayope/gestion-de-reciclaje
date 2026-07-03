import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ChangePasswordController extends GetxController {
  //TODO: Implement ChangePasswordController

  final count = 0.obs;
  final isFirstLogin = true.obs;
  final formKey = GlobalKey<FormState>();

  final theme = ThemeData().obs;
  final isDark = false.obs;
  final changing = false.obs;

  final currentPassword = TextEditingController();
  final newPassword = TextEditingController();
  final confirmPassword = TextEditingController();
  final hiddenCurrentPassword = true.obs;
  final hiddenConfirmPassword = true.obs;

  bool get hasMinLength => newPassword.text.length >= 8;
  bool get hasUppercase => RegExp(r'[A-Z]').hasMatch(newPassword.text);
  bool get hasNumber => RegExp(r'[0-9]').hasMatch(newPassword.text);
  bool get hasSpecial =>
      RegExp(r'[!@#\$%^&*(),.?":{}|<>_\-]').hasMatch(newPassword.text);

  int get strengthScore =>
      [hasMinLength, hasUppercase, hasNumber, hasSpecial]
          .where((r) => r)
          .length;

  bool get passwordsMatch =>
      newPassword.text.isNotEmpty &&
      newPassword.text == confirmPassword.text;


  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    theme.value =  Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
  }

  Future<void> changePassword() async {
    Get.offAllNamed(Routes.LOGIN);
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
