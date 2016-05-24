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

public class FragmentContact$$ViewBinder<T extends FragmentContact> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131493059, "field 'refreshLayout'");
    target.refreshLayout = finder.castView(view, 2131493059, "field 'refreshLayout'");
    view = finder.findRequiredView(source, 2131493060, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131493060, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131493029, "field 'msgTipsView'");
    target.msgTipsView = finder.castView(view, 2131493029, "field 'msgTipsView'");
    view = finder.findRequiredView(source, 2131493027, "method 'newFriend'");
    unbinder.view2131493027 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.newFriend();
      }
    });
    view = finder.findRequiredView(source, 2131493032, "method 'group'");
    unbinder.view2131493032 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.group();
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends FragmentContact> implements Unbinder {
    private T target;

    View view2131493027;

    View view2131493032;

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
      target.refreshLayout = null;
      target.recyclerView = null;
      target.msgTipsView = null;
      view2131493027.setOnClickListener(null);
      view2131493032.setOnClickListener(null);
    }
  }
}
