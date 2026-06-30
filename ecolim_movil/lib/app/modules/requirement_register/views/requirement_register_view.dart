import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/requirement_register_controller.dart';

class RequirementRegisterView extends GetView<RequirementRegisterController> {
  const RequirementRegisterView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('RequirementRegisterView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'RequirementRegisterView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
