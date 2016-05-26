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

public class ContactAddFriendActivity$$ViewBinder<T extends ContactAddFriendActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131493056, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131493056, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131493054, "field 'searchNameEdit'");
    target.searchNameEdit = finder.castView(view, 2131493054, "field 'searchNameEdit'");
    view = finder.findRequiredView(source, 2131493055, "method 'search'");
    unbinder.view2131493055 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.search(p0);
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends ContactAddFriendActivity> implements Unbinder {
    private T target;

    View view2131493055;

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
      target.recyclerView = null;
      target.searchNameEdit = null;
      view2131493055.setOnClickListener(null);
    }
  }
}
