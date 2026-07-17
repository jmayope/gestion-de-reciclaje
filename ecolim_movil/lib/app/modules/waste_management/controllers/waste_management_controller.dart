import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/modules/waste_management/views/waste_management_view.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/table_type.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:ecolim_movil/models/waste.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WasteManagementController extends GetxController {
  
  final query = "".obs;
  final wastes = <Waste>[].obs;
  final filter = "".obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final userLoged = User().obs;
  final unitMeasurements = <TableType>[].obs;
  final wasteTypes = <TableType>[].obs;
  final supabase = Get.put(SupabaseService());
  final loading = true.obs;
  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    theme.value = Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    userLoged.value = await PreferenceService.getSession();
    final resultWastes = await supabase.select(WASTES, filters: {"entity_id": userLoged.value.currentEntity!.id!});
    wastes.value = (resultWastes as Iterable).map((w) => Waste.fromJson(w)).toList();
    final resultWasteTypes = await supabase.select(TYPES, filters: {"category": "TIPO_RESIDUO"});
    wasteTypes.value = (resultWasteTypes as Iterable).map((wt) => TableType.fromJson(wt)).toList();
    final resultUnitMeasurements = await supabase.select(TYPES, filters: {"category": "UNIDAD_MEDIDA"});
    unitMeasurements.value = (resultUnitMeasurements as Iterable).map((u) => TableType.fromJson(u)).toList();
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

  Future<void> goToRegisterWaste() async {
    Get.offAllNamed(Routes.WASTE_REGISTER);
  }

  List<Waste> get filtered {
    return wastes.where((w) {
      final matchesFilter = filter.isEmpty || filter.isBlank == true;
      final matchesQuery = query.trim().isEmpty ||
          w.type!.toLowerCase().contains(query.toLowerCase()) ||
          w.quantity!.toString().toLowerCase().contains(query.toLowerCase()) ||
          w.id.toString().toLowerCase().contains(query.toLowerCase());
      return matchesFilter && matchesQuery;
    }).toList();
  }

  Future<void> goToDetail(Waste waste) async {
    Get.offAllNamed(Routes.WASTE_REGISTER, arguments: {"waste": waste});
  }

  Future<void> requestWithdrawal(Waste waste) async {
    final reasonController = TextEditingController();

    final confirmed = await showModalBottomSheet<bool>(
      context: Get.context!,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => WithdrawalSheet(waste: waste, reasonController: reasonController),
    );

    if (confirmed == true) {
        final index = wastes.indexWhere((w) => w.id == waste.id);
        if (index != -1) {
          wastes[index] = Waste(
            id: waste.id,
            type: waste.type,
            quantity: waste.quantity,
            // hazardous: waste.hazardous,
            wasteGenerationDate: waste.wasteGenerationDate,
            status: waste.status,
            state: "E",
          );
        }
      // TODO: enviar solicitud de baja al backend para autorización.
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Solicitud de baja enviada. Pendiente de autorización.')),
      );
    }
  }

}
