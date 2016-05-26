// Generated code from Butter Knife. Do not modify!
package com.leon.weibook.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class UserRegisterActivity$$ViewBinder<T extends UserRegisterActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131492963, "field 'usernameEdit'");
    target.usernameEdit = finder.castView(view, 2131492963, "field 'usernameEdit'");
    view = finder.findRequiredView(source, 2131492964, "field 'passwordEdit'");
    target.passwordEdit = finder.castView(view, 2131492964, "field 'passwordEdit'");
    view = finder.findRequiredView(source, 2131492965, "field 'ensurePwdEdit'");
    target.ensurePwdEdit = finder.castView(view, 2131492965, "field 'ensurePwdEdit'");
    view = finder.findRequiredView(source, 2131492966, "field 'registerButton' and method 'register'");
    target.registerButton = finder.castView(view, 2131492966, "field 'registerButton'");
    unbinder.view2131492966 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.register();
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends UserRegisterActivity> implements Unbinder {
    private T target;

    View view2131492966;

    protected InnerUnbinder(T target) {
      this.target = target;
    }

    @Override
    public final void unbind() {
      if (target == null) throw new IllegalStateException("Bindings already cleared.");
      unbind(target);
      target = null;
    }

    protected void unbind(T target) {
      target.usernameEdit = null;
      target.passwordEdit = null;
      target.ensurePwdEdit = null;
      view2131492966.setOnClickListener(null);
      target.registerButton = null;
    }
  }
}
