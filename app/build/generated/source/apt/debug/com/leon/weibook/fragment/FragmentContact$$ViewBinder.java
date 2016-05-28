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
    view = finder.findRequiredView(source, 2131493101, "field 'refreshLayout'");
    target.refreshLayout = finder.castView(view, 2131493101, "field 'refreshLayout'");
    view = finder.findRequiredView(source, 2131493102, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131493102, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131493099, "field 'itemNewFriend' and method 'newFriend'");
    target.itemNewFriend = finder.castView(view, 2131493099, "field 'itemNewFriend'");
    unbinder.view2131493099 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.newFriend();
      }
    });
    view = finder.findRequiredView(source, 2131493100, "field 'itemgroup' and method 'group'");
    target.itemgroup = finder.castView(view, 2131493100, "field 'itemgroup'");
    unbinder.view2131493100 = view;
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

    View view2131493099;

    View view2131493100;

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
      view2131493099.setOnClickListener(null);
      target.itemNewFriend = null;
      view2131493100.setOnClickListener(null);
      target.itemgroup = null;
    }
  }
}
