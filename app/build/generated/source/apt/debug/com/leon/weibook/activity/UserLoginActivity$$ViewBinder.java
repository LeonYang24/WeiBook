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

public class UserLoginActivity$$ViewBinder<T extends UserLoginActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131492957, "field 'userNameView'");
    target.userNameView = finder.castView(view, 2131492957, "field 'userNameView'");
    view = finder.findRequiredView(source, 2131492958, "field 'passwordView'");
    target.passwordView = finder.castView(view, 2131492958, "field 'passwordView'");
    view = finder.findRequiredView(source, 2131492962, "method 'register'");
    unbinder.view2131492962 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.register();
      }
    });
    view = finder.findRequiredView(source, 2131492961, "method 'login'");
    unbinder.view2131492961 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.login();
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends UserLoginActivity> implements Unbinder {
    private T target;

    View view2131492962;

    View view2131492961;

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
      target.userNameView = null;
      target.passwordView = null;
      view2131492962.setOnClickListener(null);
      view2131492961.setOnClickListener(null);
    }
  }
}
