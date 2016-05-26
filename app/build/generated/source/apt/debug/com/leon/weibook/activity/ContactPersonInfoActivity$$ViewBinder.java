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

public class ContactPersonInfoActivity$$ViewBinder<T extends ContactPersonInfoActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131493071, "field 'allLayout'");
    target.allLayout = finder.castView(view, 2131493071, "field 'allLayout'");
    view = finder.findRequiredView(source, 2131493073, "field 'avatarView'");
    target.avatarView = finder.castView(view, 2131493073, "field 'avatarView'");
    view = finder.findRequiredView(source, 2131493074, "field 'avatarArrowView'");
    target.avatarArrowView = finder.castView(view, 2131493074, "field 'avatarArrowView'");
    view = finder.findRequiredView(source, 2131493076, "field 'usernameView'");
    target.usernameView = finder.castView(view, 2131493076, "field 'usernameView'");
    view = finder.findRequiredView(source, 2131493078, "field 'genderView'");
    target.genderView = finder.castView(view, 2131493078, "field 'genderView'");
    view = finder.findRequiredView(source, 2131493072, "field 'avatarLayout'");
    target.avatarLayout = finder.castView(view, 2131493072, "field 'avatarLayout'");
    view = finder.findRequiredView(source, 2131493077, "field 'genderLayout'");
    target.genderLayout = finder.castView(view, 2131493077, "field 'genderLayout'");
    view = finder.findRequiredView(source, 2131493080, "field 'chatBtn' and method 'startChat'");
    target.chatBtn = finder.castView(view, 2131493080, "field 'chatBtn'");
    unbinder.view2131493080 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.startChat();
      }
    });
    view = finder.findRequiredView(source, 2131493079, "field 'addFriendBtn' and method 'addFriend'");
    target.addFriendBtn = finder.castView(view, 2131493079, "field 'addFriendBtn'");
    unbinder.view2131493079 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.addFriend();
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends ContactPersonInfoActivity> implements Unbinder {
    private T target;

    View view2131493080;

    View view2131493079;

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
      target.allLayout = null;
      target.avatarView = null;
      target.avatarArrowView = null;
      target.usernameView = null;
      target.genderView = null;
      target.avatarLayout = null;
      target.genderLayout = null;
      view2131493080.setOnClickListener(null);
      target.chatBtn = null;
      view2131493079.setOnClickListener(null);
      target.addFriendBtn = null;
    }
  }
}
