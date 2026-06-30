import 'package:flutter/material.dart';

import 'package:get/get.dart';

import '../controllers/reporting_controller.dart';

class ReportingView extends GetView<ReportingController> {
  const ReportingView({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('ReportingView'),
        centerTitle: true,
      ),
      body: const Center(
        child: Text(
          'ReportingView is working',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
