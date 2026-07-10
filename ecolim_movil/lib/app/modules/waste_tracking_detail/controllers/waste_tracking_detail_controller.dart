import 'package:ecolim_movil/models/index.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WasteTrackingDetailController extends GetxController {
  

  
  final theme = ThemeData().obs;
  final isDark = false.obs;

  final waste = Waste().obs;
  final processFlows = <ProcessFlow>[].obs;
  var arguments = Get.arguments;
  late List<DateTime?> stageDates;

  @override
  void onInit() {
    super.onInit();
   initialData();
  }

  Future<void> initialData() async {
    theme.value = Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    waste.value = arguments["waste"];
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  Future<void> advanceStage() async {
    final nextIndex = 1 + 1;
    final nextStage = waste.value.processFlows![nextIndex];

    final confirmed = await showDialog<bool>(
      context: Get.context!,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: const Text('Actualizar avance'),
        content: Text('¿Confirmas que este residuo pasó a la etapa "${nextStage.currentProcessId}"?'),
        actionsPadding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
        actions: [
          OutlinedButton(
            onPressed: () => Navigator.of(ctx).pop(false),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.of(ctx).pop(true),
            child: const Text('Confirmar'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      stageDates[nextIndex] = DateTime.now();
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        SnackBar(content: Text('Actualizado a "${nextStage.currentProcessId}"')),
      );

    }
  }

}
