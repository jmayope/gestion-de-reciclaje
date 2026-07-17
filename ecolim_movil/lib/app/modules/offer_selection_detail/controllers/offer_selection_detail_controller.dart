import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/models/entity.dart';
import 'package:ecolim_movil/models/offer.dart';
import 'package:ecolim_movil/models/process_flow.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class OfferSelectionDetailController extends GetxController {
  //TODO: Implement OfferSelectionDetailController

  
  final waste = Waste().obs;
  final operations = <ProcessFlow>[].obs;
  final wasteOperations = <TableType>[].obs;
  final offers = <Offer>[].obs;
  final entities = <Entity>[].obs;
  final wasteType = TableType().obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  var arguments = Get.arguments;
  final loading = true.obs;

  final userLoged = User().obs;
  
  final supabase = Get.put(SupabaseService());
  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    userLoged.value = await PreferenceService.getSession();

    theme.value = Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;  
    waste.value = arguments["waste"];
    wasteType.value = arguments["wasteType"];
    final resultProcessFlows = await supabase.select(PROCESS_FLOWS, filters: {"waste_id": waste.value.id});
    operations.value = (resultProcessFlows as Iterable).map((o) => ProcessFlow.fromJson(o)).toList();
    final resultWasteOperations = await supabase.select(TYPES, filters: {"category": "OPERACIONES"});

    final resultOffers = await supabase.select(OFFERS, filters: {"waste_id": waste.value.id!, "process_flow_id": operations.map((o) => o.id!).toList()});
    offers.value = (resultOffers as Iterable).map((o) => Offer.fromJson(o)).toList();

    final resultEntities = await supabase.select(ENTITIES, filters: {"id": offers.map((o) => o.entityOperatorId).toList()});
    entities.value = (resultEntities as Iterable).map((e) => Entity.fromJson(e)).toList();
    operations.forEach((o) {
      int index = operations.indexOf(o);
      operations[index] = o.copyWith(offers: Optional.of(offers.where((f) => f.processFlowId == o.id).toList()));
    });

    wasteOperations.value = (resultWasteOperations as Iterable).map((o) => TableType.fromJson(o)).toList();
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

  Future<void> confirmSelection(ProcessFlow group, Offer offer) async {
    final theme = Theme.of(Get.context!);

    final confirmed = await showDialog<bool>(
      context: Get.context!,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: Text('Confirmar selección', style: theme.textTheme.titleLarge),
        content: Text(
          'Se aceptará a "${offer.entityOperatorId}" para la operación '
          '"${group.currentProcessId}" (${offer.quantity} ${waste.value.unitMeasurement}). '
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
