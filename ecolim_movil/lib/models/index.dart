export 'process_flow.dart';
export 'offer.dart';
export 'table_type.dart';
export 'plant.dart';
export 'waste.dart';
export 'waste_process.dart';
import 'package:quiver/core.dart';

T? checkOptional<T>(Optional<T?>? optional, T? Function()? def) {
  // No value given, just take default value
  if (optional == null) return def?.call();

  // We have an input value
  if (optional.isPresent) return optional.value;

  // We have a null inside the optional
  return null;
}
