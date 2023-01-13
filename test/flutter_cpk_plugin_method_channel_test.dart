import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_cpk_plugin/flutter_cpk_plugin_method_channel.dart';

void main() {
  MethodChannelFlutterCpkPlugin platform = MethodChannelFlutterCpkPlugin();
  const MethodChannel channel = MethodChannel('flutter_cpk_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
