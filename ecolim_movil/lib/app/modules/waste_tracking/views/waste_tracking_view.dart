import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_tracking_controller.dart';

class WasteTrackingView extends GetView<WasteTrackingController> {
  const WasteTrackingView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('WasteTrackingView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'WasteTrackingView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
