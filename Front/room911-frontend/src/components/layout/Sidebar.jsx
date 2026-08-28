import { NavLink } from "react-router-dom";
import { Icon } from "../ui/Icon";

export function Sidebar({ sections, user }) {
  return (
    <aside className="r911-sidebar">
      <div className="r911-brand">
        <div className="brand-mark">
          <Icon name="shield-lock" />
        </div>
        <div>
          <div className="brand-name">room_911</div>
          <div className="brand-sub">Control de Acceso ABAC</div>
        </div>
      </div>

      <nav className="r911-nav">
        {sections.map((sec, i) => (
          <div key={i}>
            {sec.label && <div className="nav-section">{sec.label}</div>}
            {sec.items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) => "nav-link-r911" + (isActive ? " active" : "")}
              >
                <Icon name={item.icon} />
                <span>{item.label}</span>
              </NavLink>
            ))}
          </div>
        ))}
      </nav>

      {user && (
        <div className="r911-sidebar-foot">
          <div className="r911-user">
            <div className="avatar">{user.initials}</div>
            <div>
              <div className="u-name">{user.name}</div>
              <div className="u-role">{user.role}</div>
            </div>
          </div>
        </div>
      )}
    </aside>
  );
}
