import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/entity_register_controller.dart';

class EntityRegisterView extends GetView<EntityRegisterController> {
  const EntityRegisterView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('EntityRegisterView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'EntityRegisterView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
