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
    view = finder.findRequiredView(source, 2131492955, "field 'userNameView'");
    target.userNameView = finder.castView(view, 2131492955, "field 'userNameView'");
    view = finder.findRequiredView(source, 2131492956, "field 'passwordView'");
    target.passwordView = finder.castView(view, 2131492956, "field 'passwordView'");
    view = finder.findRequiredView(source, 2131492960, "method 'register'");
    unbinder.view2131492960 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.register();
      }
    });
    view = finder.findRequiredView(source, 2131492959, "method 'login'");
    unbinder.view2131492959 = view;
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

    View view2131492960;

    View view2131492959;

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
      view2131492960.setOnClickListener(null);
      view2131492959.setOnClickListener(null);
    }
  }
}
