import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_publish_controller.dart';

class WastePublishView extends GetView<WastePublishController> {
  const WastePublishView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('WastePublishView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'WastePublishView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
