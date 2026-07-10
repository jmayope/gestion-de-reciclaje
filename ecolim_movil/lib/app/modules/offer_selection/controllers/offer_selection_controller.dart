import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class OfferSelectionController extends GetxController {
  //TODO: Implement OfferSelectionController

  final wastes = <Waste>[].obs;
  

  final theme = ThemeData().obs;
  final isDark = false.obs;

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

  List<Waste> get withPendingOffers =>
      wastes.where((r) => r.pendingOperationsCount! > 0).toList();

  Future<void> viewDetail(Waste waste) async {

  }
}
