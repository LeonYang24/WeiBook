// Generated code from Butter Knife. Do not modify!
package com.leon.weibook.fragment;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class FragmentConversation$$ViewBinder<T extends FragmentConversation> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131493009, "field 'imClientStateView'");
    target.imClientStateView = view;
    view = finder.findRequiredView(source, 2131493095, "field 'refreshLayout'");
    target.refreshLayout = finder.castView(view, 2131493095, "field 'refreshLayout'");
    view = finder.findRequiredView(source, 2131493096, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131493096, "field 'recyclerView'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends FragmentConversation> implements Unbinder {
    private T target;

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
      target.imClientStateView = null;
      target.refreshLayout = null;
      target.recyclerView = null;
    }
  }
}
