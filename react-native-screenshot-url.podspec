require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-screenshot-url"
  s.version      = package["version"]
  s.summary      = "Expose config variables to React Native apps"
  s.author       = "Pedro Belo"

  s.homepage     = "https://github.com/krmao/react-native-screenshot-url"

  s.license      = "MIT"
  s.ios.deployment_target = "7.0"
  s.tvos.deployment_target = "9.0"

  s.source       = { :git => "https://github.com/krmao/react-native-screenshot-url.git", :tag => "#{s.version}" }

  s.source_files  = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
end
