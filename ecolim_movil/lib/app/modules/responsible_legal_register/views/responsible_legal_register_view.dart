import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/responsible_legal_register_controller.dart';

class ResponsibleLegalRegisterView
    extends GetView<ResponsibleLegalRegisterController> {
  const ResponsibleLegalRegisterView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('ResponsibleLegalRegisterView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'ResponsibleLegalRegisterView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
