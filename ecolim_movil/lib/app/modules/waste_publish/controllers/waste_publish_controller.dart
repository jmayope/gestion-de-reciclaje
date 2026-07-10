import 'package:ecolim_movil/app/modules/waste_publish/views/waste_publish_view.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WastePublishController extends GetxController {
  
  final Set<String> selecteds = {};
  final wastes = <Waste>[].obs;
  final hasWastes = false.obs;

  final theme = ThemeData().obs;
  final isDark = false.obs;

  bool get allSelected => wastes.isNotEmpty && selecteds.length == wastes.length;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    hasWastes.value = wastes.isNotEmpty;
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

  void toggle(String id) {
    if (selecteds.contains(id)) {
      selecteds.remove(id);
    } else {
      selecteds.add(id);
    }
  
  }

  void toggleAll() {
      if (allSelected) {
        selecteds.clear();
      } else {
        selecteds
          ..clear()
          ..addAll(wastes.map((w) => w.id.toString()));
      }
  }

  Future<void> confirmPublish() async {
    final selectedWastes = wastes.where((w) => selecteds.contains(w.id.toString())).toList();

    final confirmed = await showModalBottomSheet<bool>(
      context: Get.context!,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => PublishConfirmSheet(wastes: selectedWastes),
    );

    if (confirmed == true) {
      // TODO: enviar publicación al backend (los residuos pasan a estado "Publicado").
      wastes.removeWhere((w) => selecteds.contains(w.id));
      selecteds.clear();
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Residuos publicados correctamente')),
      );
    }
  }
}
