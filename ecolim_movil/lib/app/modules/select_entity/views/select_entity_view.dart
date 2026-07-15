import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/select_entity_controller.dart';

class SelectEntityView extends GetView<SelectEntityController> {
  const SelectEntityView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SelectEntityView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'SelectEntityView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
