import 'package:flutter/foundation.dart';
import 'package:quiver/core.dart';

import 'index.dart';

@immutable
class ResultModal {

  const ResultModal({
    this.quantity,
    required this.status,
  });

  final double? quantity;
  final bool status;

  factory ResultModal.fromJson(Map<String,dynamic> json) => ResultModal(
    quantity: json['quantity'] != null ? (json['quantity'] as num).toDouble() : null,
    status: json['status'] as bool
  );
  
  Map<String, dynamic> toJson() => {
    'quantity': quantity,
    'status': status
  };

  ResultModal clone() => ResultModal(
    quantity: quantity,
    status: status
  );


  ResultModal copyWith({
    Optional<double?>? quantity,
    bool? status
  }) => ResultModal(
    quantity: checkOptional(quantity, () => this.quantity),
    status: status ?? this.status,
  );

  @override
  bool operator ==(Object other) => identical(this, other)
    || other is ResultModal && quantity == other.quantity && status == other.status;

  @override
  int get hashCode => quantity.hashCode ^ status.hashCode;
}
