// Generated code from Butter Knife. Do not modify!
package com.leon.weibook.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class MainActivity$$ViewBinder<T extends MainActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131492950, "field 'mbtnConversation'");
    target.mbtnConversation = finder.castView(view, 2131492950, "field 'mbtnConversation'");
    view = finder.findRequiredView(source, 2131492952, "field 'mbtnContact'");
    target.mbtnContact = finder.castView(view, 2131492952, "field 'mbtnContact'");
    view = finder.findRequiredView(source, 2131492954, "field 'mbtnDiscover'");
    target.mbtnDiscover = finder.castView(view, 2131492954, "field 'mbtnDiscover'");
    view = finder.findRequiredView(source, 2131492955, "field 'mbtnSetting'");
    target.mbtnSetting = finder.castView(view, 2131492955, "field 'mbtnSetting'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends MainActivity> implements Unbinder {
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
      target.mbtnConversation = null;
      target.mbtnContact = null;
      target.mbtnDiscover = null;
      target.mbtnSetting = null;
    }
  }
}
