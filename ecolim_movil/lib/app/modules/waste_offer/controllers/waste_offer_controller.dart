import 'package:ecolim_movil/app/modules/waste_offer/views/waste_offer_view.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class WasteOfferController extends GetxController {
  //TODO: Implement WasteOfferController

  final query = "".obs;
  final wastes = <Waste>[].obs;

  List<Waste> get filtereds {
    if (query.value.trim().isEmpty) return wastes;
    final q = query.value.toLowerCase();
    return wastes
        .where((Waste w) =>
            w.type!.toLowerCase().contains(q) ||
            w.id.toString().toLowerCase().contains(q))
        .toList();
  }

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

  

  Future<void> openOfferSheet(Waste waste, TableType slot) async {
    final result = await showModalBottomSheet<bool>(
      context: Get.context!,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => OfferFormSheet(waste: waste, slot: slot),
    );

    if (result == true) {
      slot = slot.copyWith(status: Optional.of(true));
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Oferta enviada. El generador decidirá quién la recibe.')),
      );
    }
  }


}
