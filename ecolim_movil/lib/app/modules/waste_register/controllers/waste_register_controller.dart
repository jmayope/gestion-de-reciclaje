import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/index.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WasteRegisterController extends GetxController {
  
  final isEditing = false.obs;
  final formKey = GlobalKey<FormState>().obs;
  final loading = false.obs;
  final wasteTypeSelected = TableType().obs;
  final wasteTypes = <TableType>[].obs;
  final unitMeasurementSelected = TableType(name: "", code: "").obs;
  final unitMeasurements = <TableType>[].obs;
  final wasteOperations = <TableType>[].obs;
  final quantity = TextEditingController();
  final observation = TextEditingController();
  final wasteGenerationDate = DateTime.now().obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final hasStorage = false.obs;
  final waste = Waste().obs;

  var arguments = Get.arguments;

  final wasteOperationSelecteds = <TableType>[].obs;

  final userLoged = User().obs;

  final supabase = Get.put(SupabaseService());

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    userLoged.value = await PreferenceService.getSession();
    
    print(waste.isBlank);
    
    final resultTypes = await supabase.select(TYPES, filters: {"category": "TIPO_RESIDUO"});
    wasteTypes.value = (resultTypes as Iterable).map((t) => TableType.fromJson(t)).toList();
    if (wasteTypes.isNotEmpty) {
      wasteTypeSelected.value = wasteTypes.first;
    }

    final resultUnits = await supabase.select(TYPES, filters: {"category": "UNIDAD_MEDIDA"});
    unitMeasurements.value = (resultUnits as Iterable).map((t) => TableType.fromJson(t)).toList();
    if (unitMeasurements.isNotEmpty) {
      unitMeasurementSelected.value = unitMeasurements.first;
    }



    final resultWasteOperations = await supabase.select(TYPES, filters: {"category": "OPERACIONES"});
    wasteOperations.value = (resultWasteOperations as Iterable).map((wo) => TableType.fromJson(wo)).toList();

    theme.value =  Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;

    if (arguments != null) {
      waste.value = arguments["waste"];

      wasteTypeSelected.value = wasteTypes.singleWhere((wt) => wt.code! == waste.value.type);
      unitMeasurementSelected.value = unitMeasurements.singleWhere((u) => u.code == waste.value.unitMeasurement);
      quantity.value = TextEditingValue(text: waste.value.quantity.toString());
      wasteGenerationDate.value = waste.value.wasteGenerationDate!;
      hasStorage.value = waste.value.hasStorageLocation!;
    }
    
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  Future<void> pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: Get.context!,
      initialDate: wasteGenerationDate.value ?? now,
      firstDate: DateTime(now.year - 2),
      lastDate: now,
    );
    if (picked != null)  {
      wasteGenerationDate.value = picked;
    }
  }

  Future<void> handleSubmit() async {
    if (!formKey.value.currentState!.validate()) return;
    if (wasteGenerationDate.value == null) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Selecciona la fecha de generación')),
      );
      return;
    }
    if (wasteOperationSelecteds.isEmpty) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Selecciona al menos una operación para el residuo')),
      );
      return;
    }

    final newWaste = {
      "type": wasteTypeSelected.value.code,
      "quantity": num.parse(quantity.value.text),
      "unit_measurement": unitMeasurementSelected.value.code,
      "waste_generation_date": formatDateToYYYYMMDD(wasteGenerationDate.value),
      "has_storage_location": hasStorage.value,
      "state": hasStorage.value ? "R" : "P",
      "entity_id": userLoged.value.currentEntity!.id!,
      "publish_at": hasStorage.value ? formatDateToISOWithTimezone(DateTime.now()) : null,
      "created_by": userLoged.value.id!,
      "status": true
    };

    loading.value = true;
    final resultNewWaste = await supabase.insert(WASTES, newWaste);
    if (resultNewWaste.isEmpty) {
      ScaffoldMessenger.of(Get.context!).showSnackBar(
        const SnackBar(content: Text('Hubo un error al crear el residuo')),
      );
      return;
    }
    Waste wasteCreated = (resultNewWaste as Iterable).map((w) => Waste.fromJson(w)).toList().first;
    print(resultNewWaste);
    print(wasteOperationSelecteds);
    wasteOperationSelecteds.sort((a, b) => a.order! - b.order!);
    for (var i = 0; i < wasteOperationSelecteds.length; i++) {
      String previous_process_id = "";
      String next_process_id = "";
      TableType w = wasteOperationSelecteds[i];
      if (i > 0) {
        previous_process_id = wasteOperationSelecteds[i-1].code!;
      }
      if (i + 1 < wasteOperationSelecteds.length) {
        next_process_id = wasteOperationSelecteds[i+1].code!;
      }
      if (i +1 == wasteOperationSelecteds.length) {
        next_process_id = "";
      }
      final newProcessFlow = {
        "waste_id": wasteCreated.id!,
        "previous_process_id": previous_process_id,
        "current_process_id": w.code,
        "next_process_id": next_process_id,
        "quantity": wasteCreated.quantity,
        "latitude": 12.343,
        "longitude": 75.323,
        "completed": false,
        "status": true,
        "entity_generator_id": userLoged.value.id!,
        "created_by": userLoged.value.id!
      };

      final resultNewProcessFlow = await supabase.insert(PROCESS_FLOWS, newProcessFlow);
    }
    wasteOperationSelecteds.forEach((w) async {
      
    });
    ScaffoldMessenger.of(Get.context!).showSnackBar(
      const SnackBar(content: Text('Residuo creado satisfactoriamente.')),
    );
    await Future.delayed(const Duration(milliseconds: 1200));
    loading.value = false;
    Get.offAllNamed(Routes.WASTE_MANAGEMENT);
  }

  
}
