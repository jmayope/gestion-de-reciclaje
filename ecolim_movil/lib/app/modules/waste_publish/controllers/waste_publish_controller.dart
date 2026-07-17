import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/modules/waste_publish/views/waste_publish_view.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class WastePublishController extends GetxController {
  
  final selecteds = <String>[].obs;
  final wastes = <Waste>[].obs;
  final wasteTypes = <TableType>[].obs;
  final hasWastes = false.obs;

  final theme = ThemeData().obs;
  final isDark = false.obs;
  final userLoged = User().obs;
  final loading = true.obs;
  final supabase = Get.put(SupabaseService());

  bool get allSelected => wastes.isNotEmpty && selecteds.length == wastes.length;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    userLoged.value = await PreferenceService.getSession();
    final resultWasteTypes = await supabase.select(TYPES, filters: {"category": "TIPO_RESIDUO"});
    wasteTypes.value = (resultWasteTypes as Iterable).map((wt) => TableType.fromJson(wt)).toList();
    final resultWastes = await supabase.select(WASTES, filters: {"entity_id": userLoged.value.currentEntity!.id, "state": "R"});
    wastes.value = (resultWastes as Iterable).map((w) => Waste.fromJson(w)).toList();
    hasWastes.value = wastes.isNotEmpty;
    theme.value = Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    loading.value = false;
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  Future<void> toggle(String id) async {
    if (selecteds.contains(id)) {
      selecteds.remove(id);
    } else {
      selecteds.add(id);
    }
    selecteds.refresh();
  }

  Future<void> toggleAll() async {
      if (allSelected) {
        selecteds.clear();
      } else {
        selecteds
          ..clear()
          ..addAll(wastes.map((w) => w.id.toString()));
      }
  }

  Future<void> publish() async {

  }

  Future<void> confirmPublish() async {
    final selectedWastes = wastes.where((w) => selecteds.contains(w.id.toString())).toList();

    final confirmed = await showModalBottomSheet<bool>(
      context: Get.context!,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => PublishConfirmSheet(wastes: selectedWastes, wasteTypes: wasteTypes),
    );

    if (confirmed == true) {

      List<Waste> wastedsSelected = wastes.where((w) => selecteds.contains(w.id.toString())).toList();

      for (var i = 0; i < wastedsSelected.length; i++) {
        Waste waste = wastedsSelected[i];
        final resultWasteToUpdate = await supabase.update(WASTES, waste.id!, {"state": "P"});
      }

      wastes.removeWhere((w) => selecteds.contains(w.id.toString()));

      selecteds.clear();
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Residuos publicados correctamente')),
      );
    }
  }
}
