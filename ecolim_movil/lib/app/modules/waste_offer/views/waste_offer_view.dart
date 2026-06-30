import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/waste_offer_controller.dart';

class WasteOfferView extends GetView<WasteOfferController> {
  const WasteOfferView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('WasteOfferView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'WasteOfferView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
