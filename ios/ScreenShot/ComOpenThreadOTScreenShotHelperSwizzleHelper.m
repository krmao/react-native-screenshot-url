#import "ComOpenThreadOTScreenShotHelperSwizzleHelper.h"
#import <objc/runtime.h>
#import <objc/message.h>

@implementation ComOpenThreadOTScreenShotHelperSwizzleHelper

+ (void)swizzClass:(Class)c selector:(SEL)orig selector:(SEL)replace
{
    Method origMethod = class_getInstanceMethod(c, orig);
    Method newMethod = class_getInstanceMethod(c, replace);
    if(class_addMethod(c, orig, method_getImplementation(newMethod), method_getTypeEncoding(newMethod)))
        class_replaceMethod(c, replace, method_getImplementation(origMethod), method_getTypeEncoding(origMethod));
    else
        method_exchangeImplementations(origMethod, newMethod);
}

@end
