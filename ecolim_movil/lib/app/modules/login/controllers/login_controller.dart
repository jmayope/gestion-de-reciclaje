import 'dart:math';

import 'package:ecolim_movil/app/data/constants.dart';
import 'package:ecolim_movil/app/data/services/preference.service.dart';
import 'package:ecolim_movil/app/data/services/supabase.service.dart';
import 'package:ecolim_movil/app/routes/app_pages.dart';
import 'package:ecolim_movil/models/entity.dart';
import 'package:ecolim_movil/models/entity_user.dart';
import 'package:ecolim_movil/models/user.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quiver/core.dart';

class LoginController extends GetxController {
  //TODO: Implement LoginController

  final count = 0.obs;
  final theme = ThemeData().obs;
  final isDark = false.obs;
  final formKey = GlobalKey<FormState>();
  final username = TextEditingController();
  final password = TextEditingController();
  final hiddenPassword = true.obs;
  final logging = false.obs;

  final supabase = Get.put(SupabaseService());

  final userLoged = User().obs;

  @override
  void onInit() {
    super.onInit();
    initialData();
  }

  Future<void> initialData() async {
    
    theme.value = Theme.of(Get.context!);
    isDark.value = theme.value.brightness == Brightness.dark;
    // username.value = TextEditingValue(text: "jorgemayo.pe");
    // password.value = TextEditingValue(text: "123456.@");
  }

  @override
  void onReady() {
    super.onReady();
  }

  @override
  void onClose() {
    super.onClose();
  }

  void increment() => count.value++;

  Future<void> login() async {
    try {
      logging.value = true;
      final resultLogin = await supabase.select(USERS, filters: {'username': username.value.text, 'password': password.value.text});
      List<User> userFounds = resultLogin.map((u) => User.fromJson(u)).toList();
      if (userFounds.isEmpty) {
        ScaffoldMessenger.of(Get.context!).showSnackBar(
          const SnackBar(content: Text('Credenciales incorrectas')),
        );
        return;
      }
      
      userLoged.value = userFounds.first;

      if (userLoged.value.isPrincipal!) {
        ScaffoldMessenger.of(Get.context!).showSnackBar(
          const SnackBar(content: Text('Bienvenido Administrador')),
        );
        Get.offAllNamed(Routes.HOME);
        return;
      }

      print(userLoged.toJson());
      final resultEntityUsers = await supabase.select(ENTITY_USERS, filters: {"user_id": userLoged.value.id!, "status": true});
      if (resultEntityUsers.isEmpty) {
        ScaffoldMessenger.of(Get.context!).showSnackBar(
          const SnackBar(content: Text('No tienes entidades asignadas.')),
        );
        return;
      }
      print(resultEntityUsers);
      List<EntityUser> entityUsers = (resultEntityUsers as Iterable).map((r) => EntityUser.fromJson(r)).toList();
      print(entityUsers.first.entityId!);
      final resultEntities = await supabase.select(ENTITIES, filters: {"id": entityUsers.first.entityId! });
      print(resultEntities);
      if (resultEntities.isEmpty) {
        ScaffoldMessenger.of(Get.context!).showSnackBar(
          const SnackBar(content: Text('No tienes entidades asignadas.')),
        );
        return;
      }
      List<Entity> entities = (resultEntities as Iterable).map((e) => Entity.fromJson(e)).toList();

      userLoged.value = userLoged.value.copyWith(entities: Optional.of(entities));
      if (userLoged.value.entities!.length == 1) {
        userLoged.value = userLoged.value.copyWith(currentEntity: Optional.of(userLoged.value.entities!.first));
        bool saved = await PreferenceService.setSession(userLoged.value);
        if (userLoged.value.currentEntity!.type == "G") {
          Get.offAllNamed(Routes.SELECT_PLANT);
        } else {
          Get.offAllNamed(Routes.HOME);
        }
      } else {
        bool saved = await PreferenceService.setSession(userLoged.value);
        Get.offAllNamed(Routes.SELECT_ENTITY);
      }
    } catch (e) {
      print("$e");
    } finally {
      logging.value = false;
    }

    // bool needChangePassword = false; //  Random().nextInt(2) > 1;
    // if (needChangePassword) {
    //   Get.offAllNamed(Routes.CHANGE_PASSWORD);
    // } else {
    //   // bool isGenerator = Random().nextInt(2) > 1;
    //   bool isGenerator = true;
    //   if (isGenerator) {
    //     Get.offAllNamed(Routes.SELECT_PLANT);
    //     // bool selectPlant = Random().nextInt(3) > 1;
    //     // if (selectPlant) {
    //     // }
    //   } else {
    //     Get.offAllNamed(Routes.HOME);
    //   }
    // }
  }
}
