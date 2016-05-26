// Generated code from Butter Knife. Do not modify!
package com.leon.weibook.fragment;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class FragmentSetting$$ViewBinder<T extends FragmentSetting> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131493100, "field 'avatarView'");
    target.avatarView = finder.castView(view, 2131493100, "field 'avatarView'");
    view = finder.findRequiredView(source, 2131493101, "field 'userNameView'");
    target.userNameView = finder.castView(view, 2131493101, "field 'userNameView'");
    view = finder.findRequiredView(source, 2131493102, "method 'onNotifySettingClick'");
    unbinder.view2131493102 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onNotifySettingClick();
      }
    });
    view = finder.findRequiredView(source, 2131493104, "method 'onLogoutClick'");
    unbinder.view2131493104 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onLogoutClick();
      }
    });
    view = finder.findRequiredView(source, 2131493103, "method 'onCheckUpdateClick'");
    unbinder.view2131493103 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onCheckUpdateClick();
      }
    });
    view = finder.findRequiredView(source, 2131493099, "method 'onAvatarClick'");
    unbinder.view2131493099 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onAvatarClick();
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends FragmentSetting> implements Unbinder {
    private T target;

    View view2131493102;

    View view2131493104;

    View view2131493103;

    View view2131493099;

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
      target.avatarView = null;
      target.userNameView = null;
      view2131493102.setOnClickListener(null);
      view2131493104.setOnClickListener(null);
      view2131493103.setOnClickListener(null);
      view2131493099.setOnClickListener(null);
    }
  }
}
