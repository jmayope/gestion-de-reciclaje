import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/select_plant_controller.dart';

class SelectPlantView extends GetView<SelectPlantController> {
  const SelectPlantView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SelectPlantView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'SelectPlantView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
