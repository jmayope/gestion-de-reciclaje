import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/models/offer.dart';
import 'package:ecolim_movil/models/process_flow.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class OfferSelectionController extends GetxController {
  //TODO: Implement OfferSelectionController

  final wastes = <Waste>[].obs;
  final wasteTypes = <TableType>[].obs;
  final offers = <Offer>[].obs;
  final loading = true.obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final userLoged = User().obs;
  final supabase = Get.put(SupabaseService());
  final operations = <ProcessFlow>[].obs;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    userLoged.value = await PreferenceService.getSession();
    final resultWastes = await supabase.select(WASTES, filters: {"entity_id": userLoged.value.currentEntity!.id!, "state": "P"});
    wastes.value = (resultWastes as Iterable).map((w) => Waste.fromJson(w)).toList();
    final resultWasteTypes = await supabase.select(TYPES, filters: {"category": "TIPO_RESIDUO"});
    wasteTypes.value = (resultWasteTypes as Iterable).map((wt) => TableType.fromJson(wt)).toList();

    final resultOperations = await supabase.select(PROCESS_FLOWS, filters: {"waste_id": wastes.map((w) => w.id).toList()});
    operations.value = (resultOperations as Iterable).map((o) => ProcessFlow.fromJson(o)).toList();
    final resultOffers = await supabase.select(OFFERS, filters: {"waste_id": wastes.map((w) => w.id).toList()});
    offers.value = (resultOffers as Iterable).map((o) => Offer.fromJson(o)).toList();
    print(operations.length);
    wastes.forEach((w) {
      int index = wastes.indexOf(w);
      List<ProcessFlow> o = operations.where((o) => o.wasteId == w.id).toList();
      List<Offer> b = offers.where((o) => o.wasteId == w.id).toList();
      wastes[index] = w.copyWith(processFlows: Optional.of(o), pendingOperationsCount: Optional.of(0), offers: Optional.of(b));
    });

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

  List<Waste> get withPendingOffers =>
      wastes.where((r) => r.pendingOperationsCount! > 0).toList();

  Future<void> viewDetail(Waste waste) async {

  }
}
