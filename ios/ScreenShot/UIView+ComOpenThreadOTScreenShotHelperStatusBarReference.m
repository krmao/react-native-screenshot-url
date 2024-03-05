#import <UIKit/UIKit.h>

static UIView *statusBarInstance = nil;

#import "UIView+ComOpenThreadOTScreenShotHelperStatusBarReference.h"
#import "ComOpenThreadOTScreenShotHelperSwizzleHelper.h"

@implementation UIView (ComOpenThreadOTScreenShotHelperStatusBarReference)

+ (UIView *)statusBarInstance_ComOpenThreadOTScreenShotHelper
{
    return statusBarInstance;
}

+ (void)load
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class statusBarClass = NSClassFromString(@"UIStatusBar");
        [ComOpenThreadOTScreenShotHelperSwizzleHelper swizzClass:statusBarClass
                           selector:@selector(setFrame:)
                           selector:@selector(setFrameIntercept_ComOpenThreadOTScreenShotHelper:)];
        [ComOpenThreadOTScreenShotHelperSwizzleHelper swizzClass:statusBarClass
                           selector:NSSelectorFromString(@"dealloc")
                           selector:@selector(deallocIntercept_ComOpenThreadOTScreenShotHelper)];
    });
}

- (void)setFrameIntercept_ComOpenThreadOTScreenShotHelper:(CGRect)frame
{
    [self setFrameIntercept_ComOpenThreadOTScreenShotHelper:frame];
    statusBarInstance = self;
}

- (void)deallocIntercept_ComOpenThreadOTScreenShotHelper
{
    statusBarInstance = nil;
    [self deallocIntercept_ComOpenThreadOTScreenShotHelper];
}

@end
