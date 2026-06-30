import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_management_controller.dart';

class WasteManagementView extends GetView<WasteManagementController> {
  const WasteManagementView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('WasteManagementView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'WasteManagementView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
