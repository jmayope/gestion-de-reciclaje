import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/modules/waste_offer/views/waste_offer_view.dart';
import 'package:ecolim_movil/models/entity.dart';
import 'package:ecolim_movil/models/process_flow.dart';
import 'package:ecolim_movil/models/result_modal.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class WasteOfferController extends GetxController {

  final userLoged = User().obs;
  final query = "".obs;
  final wastes = <Waste>[].obs;
  final wasteTypes = <TableType>[].obs;
  final wasteOperations = <TableType>[].obs;
  final operations = <ProcessFlow>[].obs;
  final entities = <Entity>[].obs;
  final loading = true.obs;
  final supabase = Get.put(SupabaseService());

  List<Waste> get filtereds {
    if (query.value.trim().isEmpty) return wastes;
    final q = query.value.toLowerCase();
    return wastes
        .where((Waste w) =>
          w.type!.toLowerCase().contains(q) ||
          w.quantity!.toString().contains(q) ||
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
    userLoged.value = await PreferenceService.getSession();
    final resultWastes = await supabase.select(WASTES, filters: {"state": "P", "status": true});
    wastes.value = (resultWastes as Iterable).map((w) => Waste.fromJson(w)).toList();

    final resultWasteTypes = await supabase.select(TYPES, filters: {"category": "TIPO_RESIDUO"});
    wasteTypes.value = (resultWasteTypes as Iterable).map((wt) => TableType.fromJson(wt)).toList();

    final resultWasteOperations = await supabase.select(TYPES, filters: {"category": "OPERACIONES"});
    wasteOperations.value = (resultWasteOperations as Iterable).map((wo) => TableType.fromJson(wo)).toList();

    final resultOperations = await supabase.select(PROCESS_FLOWS, filters: {"waste_id": wastes.map((w) => w.id).toList()});
    operations.value = (resultOperations as Iterable).map((o) => ProcessFlow.fromJson(o)).toList();

    final resultEntities = await supabase.select(ENTITIES, filters: {"id": wastes.map((w) => w.entityId).toList()});
    entities.value = (resultEntities as Iterable).map((e) => Entity.fromJson(e)).toList();

    wastes.forEach((w) {
      int index = wastes.indexOf(w);
      wastes[index] = w.copyWith(processFlows: Optional.of(operations.where((o) => o.wasteId == w.id).toList()), operations: Optional.of(wasteOperations.where((wo) => operations.map((x) => x.currentProcessId).toList().contains(wo.code)).toList()));
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


  Future<void> openOfferSheet(Waste waste, TableType wasteType, Entity entity, TableType slot) async {
    final result = await showModalBottomSheet<ResultModal>(
      context: Get.context!,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => OfferFormSheet(waste: waste, wasteType: wasteType, entity: entity, slot: slot),
    );

    if (result!.status) {
      ProcessFlow currentProcess = waste.processFlows!.singleWhere((pf) => pf.currentProcessId == slot.code);
      final newWasteOffer = {
        "waste_id": waste.id!,
        "quantity": result.quantity,
        "status": true,
        "entity_operator_id": userLoged.value.currentEntity!.id!,
        "accepted": false,
        "process_flow_id": currentProcess.id,
        "created_by": userLoged.value.id
      };

      final resultWasteOffer = await supabase.insert(OFFERS, newWasteOffer);
      
      if (resultWasteOffer.isEmpty) {
        ScaffoldMessenger.of(Get.context!).showSnackBar(
          const SnackBar(content: Text('Hubo un error al momento de guardar tu oferta.')),
        );
        return;
      }
      slot = slot.copyWith(status: Optional.of(true));
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Oferta enviada. El generador decidirá quién la recibe.')),
      );
    }
  }


}
