import 'package:ecolim_movil/models/offer.dart';
import 'package:ecolim_movil/models/process_flow.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class OfferSelectionDetailController extends GetxController {
  //TODO: Implement OfferSelectionDetailController

  
  final waste = Waste().obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  var arguments = Get.arguments;

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

  Future<void> confirmSelection(ProcessFlow group, Offer offer) async {
    final theme = Theme.of(Get.context!);

    final confirmed = await showDialog<bool>(
      context: Get.context!,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: Text('Confirmar selección', style: theme.textTheme.titleLarge),
        content: Text(
          'Se aceptará a "${offer.operatorId}" para la operación '
          '"${group.currentProcessId}" (${offer.quantity} Tn). '
          'Los demás operadores que ofertaron por esta operación serán descartados. '
          'Esta acción no se puede deshacer.',
          style: theme.textTheme.bodyMedium,
        ),
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
  }
}
